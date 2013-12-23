package com.imseam.raptor.cluster.redis.jedis.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

import com.imseam.cluster.ClusterLockException;
import com.imseam.cluster.IClusterCache;
import com.imseam.cluster.IClusterList;
import com.imseam.cluster.IClusterMap;
import com.imseam.cluster.IClusterSet;
import com.imseam.cluster.IClusterTransaction;
import com.imseam.cluster.IFutureResult;
import com.imseam.common.util.ExceptionUtil;
import com.imseam.raptor.cluster.redis.jedis.cache.collection.JedisClusterList;
import com.imseam.raptor.cluster.redis.jedis.cache.collection.JedisClusterMap;
import com.imseam.raptor.cluster.redis.jedis.cache.collection.JedisClusterSet;

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
	public <T> void put(final String key, final T value) throws ClusterLockException {
		
		doUpdateCommand(new IJedisCommand<T>(){
			@Override
			public T doImmediate(Jedis jedis) {
				jedis.set(ByteUtils.toBytes(key), ByteUtils.serialize(value));
				return null;
			}
			@Override
			public T doInTransaction(Transaction transaction) {
				transaction.set(ByteUtils.toBytes(key), ByteUtils.serialize(value));
				return null;
			}
			
		}, key);
	}

	@Override
	public <T> T putIfAbsent(final String key, final T value) throws ClusterLockException{
		
		return doUpdateCommand(new IJedisCommand<T>(){
			@Override
			public T doImmediate(Jedis jedis) {
				if(jedis.setnx(ByteUtils.toBytes(key), ByteUtils.serialize(value)) == 1){
					return null;
				}
				return ByteUtils.deserialize(jedis.get(ByteUtils.toBytes(key)));
			}
			@Override
			public T doInTransaction(Transaction transaction) {
				transaction.setnx(ByteUtils.toBytes(key), ByteUtils.serialize(value));
				return null;
			}
			
		}, key);
		
	}

	@Override
	public void remove(final String...keys) throws ClusterLockException {
//		Remove.at(key).doCommandWithoutTransaction(jedis);
		
		doUpdateCommand(new IJedisCommand<Object>(){
			@Override
			public Object doImmediate(Jedis jedis) {
				jedis.del(keys);
				return null;
			}
			@Override
			public Response<Long> doInTransaction(Transaction transaction) {
				return transaction.del(keys);
			}
			
		}, keys);
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
	public <T> List<IFutureResult<T>> getInFuture(String... keys) {
		List<IFutureResult<T>> resultList = new ArrayList<IFutureResult<T>>();

		for(final String key : keys){
			resultList.add( AbstractFutureResult.<T>getObjectInFuture(this, new AbstractFutureGetCommand<Response<String>>(){
				@Override
				public Response<String> doInTransaction(Transaction transaction) {
					return transaction.get(key);
				}
			}));
		}
		return resultList;

		
	}

	@Override
	public <T> IFutureResult<T> getInFuture(final String key) {
		return AbstractFutureResult.<T>getObjectInFuture(this, new AbstractFutureGetCommand<Response<String>>(){
			@Override
			public Response<String> doInTransaction(Transaction transaction) {
				return transaction.get(key);
			}
		});
		
	}

	
	@Override
	public void lock(String... keys) throws ClusterLockException {
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
	
	public <T> T doUpdateCommand(IJedisCommand<T> updateCommand, String... key) throws ClusterLockException{
		JedisLockHolder lockHolder = currentLockHolder();
		
		lockHolder.lock(key);
		JedisTransaction transaction = currentTransaction();
		if(transaction == null){
			try{
				return updateCommand.doImmediate(jedis);
			}finally{
				lockHolder.unlock(key);
			}
		}else{
			transaction.onCommand(updateCommand);
		}
		return null;
	}

	public <T> void doFutureGetCommand(IJedisCommand<T> getCommand){
		
		JedisTransaction transaction = currentTransaction();
		
		if(transaction == null) ExceptionUtil.createRuntimeException("The future get command must be in a transaction");
		
		transaction.onCommand(getCommand);
		
	}

	@Override
	public <T> IClusterSet<T> getSet(String key) {
		return new JedisClusterSet<T>(this, key);
	}

	@Override
	public <T> IClusterMap<T> getMap(String key) {
		return new JedisClusterMap<T>(this, key);
	}

	@Override
	public <T> IClusterList<T> getList(String key) {
		return new JedisClusterList<T>(this, key);
	}

	
}
