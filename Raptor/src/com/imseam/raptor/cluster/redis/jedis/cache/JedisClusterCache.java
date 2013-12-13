package com.imseam.raptor.cluster.redis.jedis.cache;

import java.util.List;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.Jedis;

import com.imseam.cluster.IClusterCache;
import com.imseam.cluster.IClusterTransaction;
import com.imseam.cluster.LockException;
import com.imseam.raptor.cluster.redis.jedis.cache.commands.Put;
import com.imseam.raptor.cluster.redis.jedis.cache.commands.PutIfAbsent;
import com.imseam.raptor.cluster.redis.jedis.cache.commands.Remove;

public class JedisClusterCache implements IClusterCache {
	
	private static Log log = LogFactory.getLog(JedisClusterCache.class);

	private JedisCachePool pool = null;
	private final Jedis jedis;
	private Stack<JedisTransaction> transactionStack = new Stack<JedisTransaction>();
	private JedisLockHolder lockHolder;
	
	
	JedisClusterCache(Jedis jedis, JedisCachePool pool){
		this.jedis = jedis;
		this.pool = pool;
		this.lockHolder = new JedisLockHolder(jedis, null);
	}
	
	@Override
	public void releaseToPool() {

		try {
			if (!transactionStack.isEmpty()) {
				log.warn("The transaction is not empty, all the pending transation will be rollback");
			}

			while (transactionStack.isEmpty()) {
				JedisTransaction transaction = (JedisTransaction) transactionStack.pop();
				transaction.rollback();
			}
		} finally {
			pool.releaseToPool(jedis);
		}
	}
	
	public JedisTransaction currentTransaction(){
		return this.transactionStack.peek();
	}

	public JedisLockHolder currentLockHolder(){
		JedisTransaction transaction = this.transactionStack.peek();
		if(transaction == null){
			return this.lockHolder;
		}else{
			return transaction.getLockHolder();
		}
	}

	
	public Jedis getJedis(){
		return jedis;
	}

	@Override
	public <T> void put(String key, T obj) throws LockException {
		Put.at(key, obj).doCommand(this);
	}

	@Override
	public <T> T putIfAbsent(String key, T value) throws LockException{
		return (T) PutIfAbsent.at(key, value).doCommand(this);
	}

	@Override
	public void remove(String...key) {
		Remove.at(key).doCommandWithoutTransaction(jedis);
	}

	@Override
	public <T> List<T> get(String...keys) {
		assert(keys != null);
		assert(keys.length > 0);
		
		List<byte[]> bytesList = jedis.mget(ByteUtils.toBytesArray(keys));
		
		if(bytesList == null) return null;

		return ByteUtils.deserialize(bytesList);
	}

	@Override
	public <T> T get(String key) {
		assert(key != null);

		byte[] bytes = jedis.get(ByteUtils.toBytes(key));
		
		if(bytes == null) return null;

		return ByteUtils.deserialize(bytes);
	}

	@Override
	public void lock(String... keys) throws LockException {
		currentLockHolder().lock(keys);
	}

	@Override
	public void unlock(String... keys) {
		currentLockHolder().unlock(keys);
	}

	@Override
	public IClusterTransaction startTransaction() {
		JedisLockHolder currentLockHolder = this.transactionStack.peek().getLockHolder();
		JedisTransaction newTransaction = new JedisTransaction(jedis, currentLockHolder);
		
		this.transactionStack.add(newTransaction);
		return newTransaction;
	}
	
}
