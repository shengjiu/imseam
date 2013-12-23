package com.imseam.raptor.cluster.redis.jedis.cache.collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

import com.imseam.cluster.ClusterLockException;
import com.imseam.cluster.IClusterMap;
import com.imseam.cluster.IFutureResult;
import com.imseam.raptor.cluster.redis.jedis.cache.AbstractFutureGetCommand;
import com.imseam.raptor.cluster.redis.jedis.cache.AbstractFutureResult;
import com.imseam.raptor.cluster.redis.jedis.cache.ByteUtils;
import com.imseam.raptor.cluster.redis.jedis.cache.IJedisCommand;
import com.imseam.raptor.cluster.redis.jedis.cache.JedisClusterCache;

public class JedisClusterMap<T> extends AbstractCollection implements IClusterMap<T> {

	public JedisClusterMap(JedisClusterCache cache, String collectionKey) {
		super(cache, collectionKey);
	}

	@Override
	public void put(final String key, final T t) throws ClusterLockException {
		final String collectionKey = getCollectionKey(); 
		this.getCache().doUpdateCommand(new IJedisCommand<Object>(){
			@Override
			public Object doImmediate(Jedis jedis) {
				jedis.hset(collectionKey, key, ByteUtils.serializeToString(t));
				return null;
			}
			@Override
			public Response<Long> doInTransaction(Transaction transaction) {
				return transaction.hset(collectionKey, key, ByteUtils.serializeToString(t));
			}
		}, collectionKey);
	}

	@Override
	public void putIfAbsent(final String key, final T t)  throws ClusterLockException{
		final String collectionKey = getCollectionKey(); 
		this.getCache().doUpdateCommand(new IJedisCommand<Object>(){
			@Override
			public T doImmediate(Jedis jedis) {
//				jedis.hset(collectionKey, key, ByteUtils.serializeToString(t));
//				return null;
				if(jedis.hsetnx(collectionKey, key, ByteUtils.serializeToString(t)) == 1){
					return null;
				}
				return ByteUtils.deserializeString(jedis.hget(collectionKey, key));
				
			}
			@Override
			public Response<Long> doInTransaction(Transaction transaction) {
				return transaction.hset(collectionKey, key, ByteUtils.serializeToString(t));
			}
		}, collectionKey);
	}

	@Override
	public void remove(final String... keys)  throws ClusterLockException{
		final String collectionKey = getCollectionKey(); 
		this.getCache().doUpdateCommand(new IJedisCommand<Object>(){
			@Override
			public Object doImmediate(Jedis jedis) {
				jedis.hdel(collectionKey, keys);
				return null;
			}
			@Override
			public List<Response<Long>> doInTransaction(Transaction transaction) {
				List<Response<Long>> responseList = new ArrayList<Response<Long>>();
				for(String key : keys){
					responseList.add(transaction.hdel(collectionKey, key));
				}
				return responseList; 
			}
		}, collectionKey);

	}

	@Override
	public T get(String key) {
		return ByteUtils.deserializeString(getJedis().hget(getCollectionKey(), key));
	}

	@Override
	public Map<String, T> getAll() {
		
		Map<String, String> stringMap = getJedis().hgetAll(getCollectionKey());
		if(stringMap == null || stringMap.size() == 0) return null;
		Map<String, T> tMap = new HashMap<String, T>();
		for(String key : stringMap.keySet()){
			tMap.put(key, ByteUtils.<T>deserializeString(stringMap.get(key)));
		}
		return tMap;
	}

	@Override
	public int size() {
		return getJedis().hlen(getCollectionKey()).intValue();
	}

	@Override
	public IFutureResult<T> getInFuture(final String key) {

		final String collectionKey = getCollectionKey();

		return AbstractFutureResult.getObjectInFuture(this.getCache(), new AbstractFutureGetCommand<Response<String>>(){
			@Override
			public Response<String> doInTransaction(Transaction transaction) {
				return transaction.hget(collectionKey, key);
			}
		});		
	}

	@Override
	public IFutureResult<Map<String, T>> getAllInFuture() {
		
		final String collectionKey = getCollectionKey();

		return AbstractFutureResult.getMapInFuture(this.getCache(), new AbstractFutureGetCommand<Response<Map<String, String>>>(){
			@Override
			public Response<Map<String, String>> doInTransaction(Transaction transaction) {
				return transaction.hgetAll(collectionKey);
			}
		});		
		

	}

	@Override
	public IFutureResult<Integer> sizeInFuture() {
		
		final String collectionKey = getCollectionKey();
		
		return AbstractFutureResult.getIntegerInFuture(this.getCache(), new AbstractFutureGetCommand<Response<Long>>(){
			@Override
			public Response<Long> doInTransaction(Transaction transaction) {
				
				return transaction.hlen(collectionKey);
			}
		});
		
	}
	

}
