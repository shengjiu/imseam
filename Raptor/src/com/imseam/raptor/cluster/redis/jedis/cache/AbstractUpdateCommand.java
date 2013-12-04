package com.imseam.raptor.cluster.redis.jedis.cache;

import java.util.HashSet;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

public abstract class AbstractUpdateCommand{
	
	private Set<String> lockKeyList = new HashSet<String>();
	protected AbstractUpdateCommand(String ... lockKeys){
		if(lockKeys != null){
			for(String lockKey:lockKeys){
				lockKeyList.add(lockKey);
			}
		}
	}

	void addAdditionalLockKey(String lockKey){
		lockKeyList.add(lockKey);
	}
	
	
	String[] getLockKeys(){
		return lockKeyList.toArray(new String[lockKeyList.size()]);
	}
	
	
	abstract public void doCommandWithoutTransaction(Jedis jedis);
	
	abstract public void doCommandWithTransaction(Transaction transaction);
	

}
