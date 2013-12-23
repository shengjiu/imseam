package com.imseam.raptor.cluster.redis.jedis.cache.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

import com.imseam.cluster.ClusterLockException;
import com.imseam.cluster.IClusterList;
import com.imseam.cluster.IFutureResult;
import com.imseam.raptor.cluster.redis.jedis.cache.AbstractFutureGetCommand;
import com.imseam.raptor.cluster.redis.jedis.cache.AbstractFutureResult;
import com.imseam.raptor.cluster.redis.jedis.cache.ByteUtils;
import com.imseam.raptor.cluster.redis.jedis.cache.IJedisCommand;
import com.imseam.raptor.cluster.redis.jedis.cache.JedisClusterCache;

public class JedisClusterList<T> extends AbstractCollection implements IClusterList<T> {

	public JedisClusterList(JedisClusterCache cache, String collectionKey) {
		super(cache, collectionKey);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void add(final T... ts) throws ClusterLockException {
		final String collectionKey = getCollectionKey(); 
		this.getCache().doUpdateCommand(new IJedisCommand<Object>(){
			@Override
			public Object doImmediate(Jedis jedis) {
				jedis.lpush(collectionKey, ByteUtils.serializeToStrings(ts));
				return null;
			}
			@Override
			public List<Response<Long>> doInTransaction(Transaction transaction) {
				List<Response<Long>> responseList = new ArrayList<Response<Long>>();
				for(T t :ts){
					responseList.add(transaction.lpush(collectionKey, ByteUtils.serializeToString(t)));
				}
				return responseList; 
			}
		}, collectionKey);
	}


	@Override
	public void remove(final int...indexes)  throws ClusterLockException{
		final String collectionKey = getCollectionKey(); 
		this.getCache().doUpdateCommand(new IJedisCommand<Object>(){
			@Override
			public Object doImmediate(Jedis jedis) {
				Pipeline pipeline= jedis.pipelined();
				pipeline.multi();
				String uniqueValue = UUID.randomUUID().toString();
				for(int index : indexes){
					pipeline.lset(collectionKey, index, uniqueValue);
				}
				pipeline.lrem(collectionKey, 0, uniqueValue);
				pipeline.exec();
				return null;
			}
			@Override
			public Response<Long> doInTransaction(Transaction transaction) {
				String uniqueValue = UUID.randomUUID().toString();
				for(int index : indexes){
					transaction.lset(collectionKey, index, uniqueValue);
				}
				return transaction.lrem(collectionKey, 0, uniqueValue);
			}
		}, collectionKey);

	}

	@Override
	public T get(int i) {
		return ByteUtils.deserializeString(getJedis().lindex(getCollectionKey(), (long)i));
	}

	@Override
	public List<T> getAll() {
		return ByteUtils.deserializeString(getJedis().lrange(getCollectionKey(), 1, -1));
	}

	@Override
	public int size() {
		return getJedis().llen(getCollectionKey()).intValue();
	}

	@Override
	public IFutureResult<T> getInFuture(final int i) {
		final String collectionKey = getCollectionKey();

		return AbstractFutureResult.getObjectInFuture(this.getCache(), new AbstractFutureGetCommand<Response<String>>(){
			@Override
			public Response<String> doInTransaction(Transaction transaction) {
				return transaction.lindex(collectionKey, i);
			}
		});
	}

	@Override
	public IFutureResult<List<T>> getAllInFuture() {
		final String collectionKey = getCollectionKey();
		
		return AbstractFutureResult.getListInFuture(this.getCache(), new AbstractFutureGetCommand<Response<? extends Collection<String>>>(){
			@Override
			public Response<? extends Collection<String>> doInTransaction(Transaction transaction) {
				
				return transaction.lrange(collectionKey, -1, 1);
			}
		});
	}

	@Override
	public IFutureResult<Integer> sizeInFuture() {
		final String collectionKey = getCollectionKey();
		
		return AbstractFutureResult.getIntegerInFuture(this.getCache(), new AbstractFutureGetCommand<Response<Long>>(){
			@Override
			public Response<Long> doInTransaction(Transaction transaction) {
				
				return transaction.llen(collectionKey);
			}
		});
	}

}
