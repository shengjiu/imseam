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

import com.imseam.cluster.LockException;
import com.imseam.common.util.ExceptionUtil;


public class JedisLockHolder {

	private static Log log = LogFactory.getLog(JedisClusterCache.class);

	private HashMap<String, String> keyLockMap = new HashMap<String, String>();

	private Jedis jedis;
	
	private JedisLockHolder parentLockHolder;
	
	
	private long earliestLockTimeMilliSec;

	JedisLockHolder(Jedis jedis, JedisLockHolder parentLockHolder) {
		this.jedis = jedis;
		this.parentLockHolder = parentLockHolder;
	}
	
	
	private boolean isLockHoldByParent(String key){
		if(parentLockHolder == null) return false;
			
		if(this.parentLockHolder.keyLockMap.get(key) != null) return true;
			
		return this.parentLockHolder.isLockHoldByParent(key);
	}

	String [] filterLockKeys(String [] keys){
		assert(keys != null && keys.length > 0);
		List<String> filteredLockKeyList = new ArrayList<String>();
		
		for(String key : keys){
			if(this.keyLockMap.get(key) != null){
				log.warn("The key is already locked in the same lockholder, lock key will be ignored: " + key);
				continue;
			}
			if(isLockHoldByParent(key)){
				continue;
			}
			filteredLockKeyList.add(key);
		}
		
		return filteredLockKeyList.toArray(new String[filteredLockKeyList.size()]);
	}

	String [] filterUnlockKeys(String [] keys){
		assert(keys != null && keys.length > 0);
		List<String> filteredLockKeyList = new ArrayList<String>();
		
		for(String key : keys){
			if(isLockHoldByParent(key)){
				continue;
			}
			filteredLockKeyList.add(key);
		}
		
		return filteredLockKeyList.toArray(new String[filteredLockKeyList.size()]);
	}

	public JedisActiveLocks lock(String... keys) throws LockException {
		
		if (keys == null || keys.length == 0)
			return null;

		Map<String, String> resultMap = JedisLockHelper.lock(jedis, this, keys);
		
		if(resultMap == null) return null;
		
		this.keyLockMap.putAll(resultMap);
		
		return new JedisActiveLocks(getMapKeys(resultMap));
	}
	
	private String[] getMapKeys(){
		return getMapKeys(keyLockMap);
	}

	private String[] getMapKeys(Map<String, String> map){
		return map.keySet().toArray(new String[map.size()]);
	}

	public long getEarliestLockTimeMilliSec(){
		long theTime = earliestLockTimeMilliSec;
		if(this.parentLockHolder != null){
			long theParentTime = this.parentLockHolder.getEarliestLockTimeMilliSec();
			if(theTime > theParentTime)
				theTime = theParentTime; 
		}
		return theTime;
	}
	
	void buildLockKeysIncludeParent(Map<String, String> allLocksMap){
		allLocksMap.putAll(keyLockMap);
		if(this.parentLockHolder != null){
			this.parentLockHolder.buildLockKeysIncludeParent(allLocksMap);
		}
	}
	
	boolean checkAndWatchHoldingLocks(){
		return JedisLockHelper.checkAndWatchHoldingLocks(jedis, this);
	}
	
	public HashMap<String, String> getKeyLockMap() {
		return keyLockMap;
	}

	void unlock(String... keys) {

		if (keys == null || keys.length == 0)
			return;

		String[] lockKeys = JedisLockHelper.unlock(jedis, this, keys);
		
		for (int i = 0; i < lockKeys.length; i++) {
			this.keyLockMap.remove(lockKeys[i]);
		}
	}

	void cleanLocks() {
		unlock(getMapKeys());
	}

}