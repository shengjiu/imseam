package com.imseam.raptor.cluster.redis.jedis.cache;

import java.util.ArrayList;
import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import com.imseam.cluster.ClusterLockException;
import com.imseam.cluster.IClusterTransaction;

public class JedisTransaction implements IClusterTransaction{
	private List<IJedisCommand<?>> pendingCommandList = new ArrayList<IJedisCommand<?>>();
//	private Map<IJedisCommand<?>, FutureResult<?>> commandFutureResultMap = new HashMap<IJedisCommand<?>, FutureResult<?>>();
	private final JedisLockHolder lockHolder;
	
	private final Jedis jedis;
	
	
	JedisTransaction(Jedis jedis, JedisLockHolder parentLockHolder){
		this.jedis = jedis;
		lockHolder = new JedisLockHolder(jedis, parentLockHolder);
		
	}
	
	public JedisLockHolder getLockHolder(){
		return this.lockHolder;
	}
	
	public JedisActiveLocks lock(String... keys) throws ClusterLockException {
		return lockHolder.lock(keys);
	}


	public void unlock(String... keys) {
		lockHolder.unlock(keys);
	}
	
	
	
	void onCommand(IJedisCommand<?> command) {
		pendingCommandList.add(command);
	}
//	void onCommand(FutureResult<?> futureResult, IJedisCommand<?> command) throws ClusterLockException{
//		pendingCommandList.add(command);
//		commandFutureResultMap.put(command, futureResult);
//	}

	
//	private void lockKeys(String[] keys){
//		lockHolder.lock(keys);
//	}
	
	
	public void commit() throws ClusterLockException{
		if(pendingCommandList.size() > 0){
			if(!lockHolder.checkAndWatchHoldingLocks()){
				throw new ClusterLockException("Lost lock exception before commit.");
			}
			Transaction jedisTransaction = jedis.multi();	
			for(IJedisCommand<?> command : pendingCommandList){
				
				command.doInTransaction(jedisTransaction);
			}
			jedisTransaction.exec();
		}
		
		lockHolder.cleanLocks();
	}
	
	public void rollback(){
		lockHolder.cleanLocks();
	}


	

}
