package com.imseam.raptor.cluster.redis.jedis.cache;

import com.imseam.cluster.LockException;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

public abstract class AbstractUpdateCommand{
	
	private String [] keys = null;
	
	protected AbstractUpdateCommand(String ... keys){
		this.keys = keys;
	}

	public <T extends AbstractUpdateCommand> Object doCommand(JedisClusterCache clusterCache) throws LockException{
		JedisLockHolder lockHolder = clusterCache.currentLockHolder();
		
		lockHolder.lock(keys);
		
		if(clusterCache.currentTransaction() == null){
			try{
				return doCommandWithoutTransaction(clusterCache.getJedis());
			}finally{
				lockHolder.unlock(keys);
			}
		}
		return new FutureResult((T)this);
	}
	
	public String[] getKeys(){
		return keys;
	}


	abstract public Object doCommandWithoutTransaction(Jedis jedis);
	
	abstract public void doCommandWithTransaction(Transaction transaction);
	
	
	public static class FutureResult<T extends AbstractUpdateCommand>{
		private T command;
		public FutureResult(T t){
			this.command = t;
		}
		
		public T getCommand(){
			return command;
		}
	}

}
