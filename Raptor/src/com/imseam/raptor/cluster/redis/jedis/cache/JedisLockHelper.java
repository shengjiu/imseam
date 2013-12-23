package com.imseam.raptor.cluster.redis.jedis.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import com.imseam.cluster.ClusterLockException;
import com.imseam.common.util.ExceptionUtil;

public class JedisLockHelper {
	
	private static Log log = LogFactory.getLog(JedisLockHelper.class);

	private static final String in_Lock_Seperator = ":::";
	private static final long lock_live_time_milli_sec = 120000;
	private static final long acquire_lock_expire_milli_sec = 60000;
	
	private static String createLock() {
		return UUID.randomUUID().toString() + ":::" + System.currentTimeMillis();

	}
	
	private  static  String[] buildKeyValueArray(String[] lockKeys, String lock) {
		String[] lockKeyValuess = new String[lockKeys.length * 2];
		for (int i = 0; i < lockKeys.length; i++) {
			lockKeyValuess[i * 2] = lockKeys[i];
			lockKeyValuess[i * 2 + 1] = lock;
		}
		return lockKeyValuess;
	}

	private static boolean keyExpired(String existingLock) {
		if(existingLock == null || (!existingLock.contains(in_Lock_Seperator))) return false;
		
		long lockTime = Long.parseLong(existingLock.split(in_Lock_Seperator)[1]);
		
		return (lock_live_time_milli_sec + lockTime) < System.currentTimeMillis();  

	}

	
	private static int removeExpiredKeys(Jedis jedis, String[] lockKeys, long timeOutAt){
		while (System.currentTimeMillis() < timeOutAt) {
			// check if the keys are expired if so del the expired keys
			jedis.watch(lockKeys);

			List<String> existingLockList = jedis.mget(lockKeys);
			List<String> expiredlockKeyList = new ArrayList<String>();

			for (int i = 0; i < lockKeys.length; i++) {
				if (keyExpired(existingLockList.get(i))) {
					expiredlockKeyList.add(lockKeys[i]);
				}
			}
			if (expiredlockKeyList.size() == 0) return 0;
			
			//if there are expired keys
			Transaction jedisTransaction = jedis.multi();
			jedisTransaction.del(expiredlockKeyList.toArray(new String[expiredlockKeyList.size()]));
			
			if(jedisTransaction.exec() != null) {
				return expiredlockKeyList.size();
			}
		}
		return 0;
	}

	private static String[] buildLockKeys(String[] keys) {
		String[] lockKeys = new String[keys.length];
		for (int i = 0; i < keys.length; i++) {
			lockKeys[i] = ":::jedis---locks:::" + keys[i];
		}
		return lockKeys;
	}
	

	
	public static Map<String, String> lock(Jedis jedis, JedisLockHolder lockHolder, String ... keys) throws ClusterLockException{
		if (keys == null || keys.length == 0)
			return null;

		String[] lockKeys = buildLockKeys(keys);
		
		lockKeys = lockHolder.filterLockKeys(lockKeys);
		
		if (lockKeys == null || lockKeys.length == 0)
			return null;
		
		String lockValue = createLock();

		long expireTime = System.currentTimeMillis() + acquire_lock_expire_milli_sec;

		
		while (true) {
			if (jedis.msetnx(buildKeyValueArray(lockKeys, lockValue)) == 1) {
				break;
			} else {
				if(removeExpiredKeys(jedis, lockKeys, expireTime) > 0)
					continue;
				try {
					if(System.currentTimeMillis() < expireTime){ 
						Thread.sleep(10);
					}
					else{
						String keysString = "";
						for(String key : keys){
							keysString += ", " + key; 
						}
						throw new ClusterLockException("Timed out to lock keys: " + keysString);				
					}
				} catch (InterruptedException e) {
					log.warn(e);
				}
			}
		}
		Map<String, String> keyLockMap = new HashMap<String, String>();
		for (String key : lockKeys) {
			keyLockMap.put(key, lockValue);
		}
		return keyLockMap;
	}
	
	public static String[] getValidKeyList(Jedis jedis, Map<String, String> keyLockMap, String[] lockKeys) {
		if (lockKeys == null || lockKeys.length == 0)
			return null;

		List<String> existingLockList = jedis.mget(lockKeys);

		List<String> heldLockList = new ArrayList<String>();

		for (int i = 0; i < lockKeys.length; i++) {

			String lockValue = keyLockMap.get(lockKeys[i]);

			if (lockValue == null)
				ExceptionUtil.createRuntimeException("The lock is not existing in the lockHolder");

			if (lockValue.equals(existingLockList.get(i))) {
				heldLockList.add(lockKeys[i]);
			}
		}

		return heldLockList.toArray(new String[heldLockList.size()]);

	}


	public static boolean checkAndWatchHoldingLocks(Jedis jedis, JedisLockHolder lockHolder){
		
		Map<String, String> allLocksMap = new HashMap<String, String>();
		lockHolder.buildLockKeysIncludeParent(allLocksMap);
		String[] allLockKeys = allLocksMap.keySet().toArray(new String[allLocksMap.size()]);

		if(allLockKeys.length == 0) return true;
		
		jedis.watch(allLockKeys);
		
		if((lockHolder.getEarliestLockTimeMilliSec() + lock_live_time_milli_sec) < System.currentTimeMillis()){
			return true;
		}
		
		String[] goodLockKeys = getValidKeyList(jedis, allLocksMap, allLockKeys);
		
		if(goodLockKeys == null || (goodLockKeys.length != allLockKeys.length)){
			jedis.unwatch();
			return false;
		}
		
		return true;
	}
	
	public static String[] unlock(Jedis jedis, JedisLockHolder lockHolder, String... keys) {

		if (keys == null || keys.length == 0)
			return null;

		String[] lockKeys = buildLockKeys(keys);
		
		lockKeys = lockHolder.filterUnlockKeys(lockKeys);
		
		if (lockKeys == null || lockKeys.length == 0)
			return null;
		
		while (true) {
			jedis.watch(lockKeys);
			String[] goodLockKeys = getValidKeyList(jedis, lockHolder.getKeyLockMap(), lockKeys);

			Transaction jedisTransaction = jedis.multi();
			jedisTransaction.del(goodLockKeys);

			if (jedisTransaction.exec() != null) {
				break;
			} else {
				lockKeys = goodLockKeys;
			}
		}
		return lockKeys;
	}

}
