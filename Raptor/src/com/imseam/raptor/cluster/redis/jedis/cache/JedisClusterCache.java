package com.imseam.raptor.cluster.redis.jedis.cache;

import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.Jedis;
import redis.clients.util.SafeEncoder;

import com.imseam.cluster.IClusterCache;
import com.imseam.cluster.IClusterTransaction;
import com.imseam.cluster.TimeoutForAcquireLockException;
import com.imseam.raptor.cluster.redis.jedis.JedisInstance;
import com.imseam.raptor.cluster.redis.jedis.cache.commands.Put;
import com.imseam.raptor.cluster.redis.jedis.cache.commands.Remove;
import com.imseam.serialize.SerializerUtil;

public class JedisClusterCache implements IClusterCache {
	
	private static Log log = LogFactory.getLog(JedisClusterCache.class);

	private JedisCachePool pool = null;
	private final Jedis jedis;
	private Stack<JedisTransaction> transactionStack = new Stack<JedisTransaction>();
	private JedisLockHolder lockHolder;
	
	
	JedisClusterCache(Jedis jedis, JedisCachePool pool){
		this.jedis = jedis;
		this.pool = pool;
		this.lockHolder = new JedisLockHolder(jedis);
	}
	
	@Override
	public void releaseToPoolWithCommit() {
		try {
			while (transactionStack.isEmpty()) {
				JedisTransaction transaction = (JedisTransaction) transactionStack.pop();
				transaction.commit();
			}
		} finally {
			pool.releaseToPool(jedis);
		}
	}

	
	@Override
	public void releaseToPoolWithRollback() {
		try {
			while (transactionStack.isEmpty()) {
				JedisTransaction transaction = (JedisTransaction) transactionStack.pop();
				transaction.rollback();
			}
		} finally {
			pool.releaseToPool(jedis);
		}
	}
	
	private boolean isInTransaction(){
		return this.transactionStack.size() > 0;
	}

	@Override
	public <T> void put(String key, T obj) {
		
		Put<T> putCommand = Put.at(key, obj);
		
		if(isInTransaction())
			putCommand.doCommandWithoutTransaction(jedis);
		else
			transactionStack.peek().onCommand(putCommand);
		
	}

	@Override
	public <T> T putIfAbsent(String key, T obj) {
		byte[] originalBytes = null;
		T originalObject = null;
		byte[] keyBytes = SafeEncoder.encode(key);
		Jedis jedis = null;
		try{
			jedis = JedisInstance.getJedisFromPool();
			originalBytes = jedis.get(keyBytes);
			
			if(originalBytes == null){
				jedis.set(keyBytes, SerializerUtil.serialize(obj));
			}else{
				originalObject = (T)SerializerUtil.deserialize(originalBytes);
			}
		}finally{
			JedisInstance.returnToPool(jedis);
		}
		return originalObject;
	}

	@Override
	public void remove(String key) {
		Remove removeCommand = Remove.at(key);
		
		if(isInTransaction())
			removeCommand.doCommandWithoutTransaction(jedis);
		else
			transactionStack.peek().onCommand(removeCommand);

	}

	@Override
	public <T> T get(String key) {
		byte[] originalBytes = null;
		T originalObject = null;
		byte[] keyBytes = SafeEncoder.encode(key);
		
		originalBytes = jedis.get(keyBytes);
	
		return (T)SerializerUtil.deserialize(originalBytes);
	}



	@Override
	public void lock(String... keys) throws TimeoutForAcquireLockException {
		JedisTransaction transaction = this.transactionStack.peek();
		if(transaction != null){
			transaction.lock(keys);
		}else{
			this.lockHolder.lock(keys);
		}
	}


	@Override
	public void optimisticLock(String... keys) {
		JedisTransaction transaction = this.transactionStack.peek();
		if(transaction != null){
			transaction.optimisticLock(keys);
		}else{
			this.lockHolder.optimisticLock(keys);
		}
	}

	@Override
	public void unlock(String... keys) {
		JedisTransaction transaction = this.transactionStack.peek();
		if(transaction != null){
			transaction.unlock(keys);
		}else{
			this.lockHolder.unlock(keys);
		}
	}



	@Override
	public IClusterTransaction startTransaction() {
		JedisTransaction transaction = new JedisTransaction(jedis);
		this.transactionStack.add(transaction);
		return transaction;
	}




	
}
