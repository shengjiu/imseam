package com.imseam.raptor.cluster.redis.jedis.cache;

import java.util.ArrayList;
import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import com.imseam.cluster.IClusterTransaction;
import com.imseam.cluster.TimeoutForAcquireLockException;

public class JedisTransaction implements IClusterTransaction{
	private List<AbstractUpdateCommand> pendingCommandList = new ArrayList<AbstractUpdateCommand>();
	private final JedisLockHolder lockHolder;
	
	private final Jedis jedis;
	
	
	JedisTransaction(Jedis jedis){
		this.jedis = jedis;
		lockHolder = new JedisLockHolder(jedis);
		
	}
	
	
	public void lock(String... keys) throws TimeoutForAcquireLockException {
		lockHolder.lock(keys);
	}


	public void optimisticLock(String... keys) {
		lockHolder.optimisticLock(keys);
	}

	public void unlock(String... keys) {
		lockHolder.unlock(keys);
	}
	
	void onCommand(AbstractUpdateCommand command){
		lockKeys(command.getLockKeys());
		pendingCommandList.add(command);
	}
	
	private void lockKeys(String[] keys){
		lockHolder.lockKeys(keys);
	}
	
	
	public void commit(){
		if(pendingCommandList.size() > 0){
			Transaction jedisTransaction = jedis.multi();	
			for(AbstractUpdateCommand command : pendingCommandList){
				command.doCommandWithTransaction(jedisTransaction);
			}
			jedisTransaction.exec();
		}
		
		lockHolder.cleanLocks();
	}
	
	public void rollback(){
		lockHolder.cleanLocks();
	}
	

}
