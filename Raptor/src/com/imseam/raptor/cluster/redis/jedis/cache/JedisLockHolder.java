package com.imseam.raptor.cluster.redis.jedis.cache;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.cluster.TimeoutForAcquireLockException;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

public class JedisLockHolder {
	
	private static Log log = LogFactory.getLog(JedisClusterCache.class);
	
	private List<String> lockList;
	private List<String> optimasticLockList;
	private HashMap<String, String> lockUUIDMap = new HashMap<String, String>();
	private Jedis jedis;
	
	JedisLockHolder(Jedis jedis){
		this.jedis = jedis;
	}
	
	
	void lock(String... keys) throws TimeoutForAcquireLockException {
		/**
		 * Lock expiration in miliseconds.
		 */
		int expire = 60;

		long expireTime = System.currentTimeMillis() + 60000;

		while (System.currentTimeMillis() < expireTime) {
			for (String key : keys) {
				String lockUUID = UUID.randomUUID().toString();
				if (jedis.setnx(key, lockUUID) == 1) {
					// lock acquired
					jedis.expire(key, expire);
					lockUUIDMap.put(key, lockUUID);
					continue;
				} else if (jedis.ttl(key) <= 0) {
					jedis.expire(key, expire);
				}
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				log.warn(e);
			}
		}
	}


	
	void optimisticLock(String... keys) {
		// TODO Auto-generated method stub
		
	}



	
	void unlock(String... keys) {
		
		Pipeline pipe = jedis.pipelined();

		try{
			while (true) {
				pipe.watch(keys);
				for (String key : keys) {
					String lockUUID = this.lockUUIDMap.get(key);
					if(lockUUID.equals(pipe.get(key))){
						
					}
				}
				pipe.multi();
				pipe.del(keys);
				pipe.exec();
				
//				pipe.unwatch();
			}
		}catch(Exception exp){
			
		}
		
	}
	
	
	void lockKeys(String[] keys){
		if(keys != null){
			
		}
	}
	
	void cleanLocks(){
		
	}
	
	
	

}
