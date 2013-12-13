package com.imseam.raptor.cluster.redis.jedis.cache;

import java.util.ArrayList;
import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import com.imseam.cluster.IClusterTransaction;
import com.imseam.cluster.LockException;
import com.imseam.raptor.cluster.redis.jedis.cache.commands.Put;
import com.imseam.raptor.cluster.redis.jedis.cache.commands.Remove;

public class JedisTransaction implements IClusterTransaction{
	private List<AbstractUpdateCommand> pendingCommandList = new ArrayList<AbstractUpdateCommand>();
	private final JedisLockHolder lockHolder;
	
	private final Jedis jedis;
	
	
	JedisTransaction(Jedis jedis, JedisLockHolder parentLockHolder){
		this.jedis = jedis;
		lockHolder = new JedisLockHolder(jedis, parentLockHolder);
		
	}
	
	public JedisLockHolder getLockHolder(){
		return this.lockHolder;
	}
	
	public JedisActiveLocks lock(String... keys) throws LockException {
		return lockHolder.lock(keys);
	}


	public void unlock(String... keys) {
		lockHolder.unlock(keys);
	}
	
	void onCommand(AbstractUpdateCommand command) throws LockException{
		lock(command.getKeys());
		pendingCommandList.add(command);
	}
	
//	private void lockKeys(String[] keys){
//		lockHolder.lock(keys);
//	}
	
	
	public void commit() throws LockException{
		if(pendingCommandList.size() > 0){
			if(!lockHolder.checkAndWatchHoldingLocks()){
				throw new LockException("Lost lock exception before commit.");
			}
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
