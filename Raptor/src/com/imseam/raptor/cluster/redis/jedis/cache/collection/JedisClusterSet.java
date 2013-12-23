package com.imseam.raptor.cluster.redis.jedis.cache.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

import com.imseam.cluster.ClusterLockException;
import com.imseam.cluster.IClusterSet;
import com.imseam.cluster.IFutureResult;
import com.imseam.raptor.cluster.redis.jedis.cache.AbstractFutureGetCommand;
import com.imseam.raptor.cluster.redis.jedis.cache.AbstractFutureResult;
import com.imseam.raptor.cluster.redis.jedis.cache.ByteUtils;
import com.imseam.raptor.cluster.redis.jedis.cache.IJedisCommand;
import com.imseam.raptor.cluster.redis.jedis.cache.JedisClusterCache;

public class JedisClusterSet<T> extends AbstractCollection implements IClusterSet<T> {

	public JedisClusterSet(JedisClusterCache cache, String collectionKey) {
		super(cache, collectionKey);
	}

	@Override
	public void add(final T... ts) throws ClusterLockException {
		final String collectionKey = getCollectionKey(); 
		this.getCache().doUpdateCommand(new IJedisCommand<Object>(){
			@Override
			public Object doImmediate(Jedis jedis) {
				jedis.sadd(collectionKey, ByteUtils.serializeToStrings(ts));
				return null;
			}
			@Override
			public void doInTransaction(Transaction transaction) {
				for(T t :ts){
					transaction.sadd(collectionKey, ByteUtils.serializeToString(t));
				}
			}
		}, collectionKey);
	}

	@Override
	public void remove(final T... ts) throws ClusterLockException {
		final String collectionKey = getCollectionKey(); 
		this.getCache().doUpdateCommand(new IJedisCommand<Object>(){
			@Override
			public Object doImmediate(Jedis jedis) {
				jedis.srem(collectionKey, ByteUtils.serializeToStrings(ts));
				return null;
			}
			@Override
			public void doInTransaction(Transaction transaction) {
				List<Response<Long>> responseList = new ArrayList<Response<Long>>();
				for(T t :ts){
					transaction.srem(collectionKey, ByteUtils.serializeToString(t));
				}
			}
		}, collectionKey);

	}

	@Override
	public T[] getAll() {
		List<T> allMemberList = ByteUtils.deserializeString(getJedis().smembers(getCollectionKey()));
		
		if(allMemberList == null || allMemberList.size() == 0) return null;
		
		return (T[])allMemberList.toArray(new Object[allMemberList.size()]);
	}

	@Override
	public boolean isExisting(T t) {
		return getJedis().sismember(getCollectionKey(), ByteUtils.serializeToString(t));
	}

	@Override
	public int size() {
		return getJedis().scard(getCollectionKey()).intValue();
	}

	@Override
	public IFutureResult<Boolean> isExistingInFuture(final T t) {

		final String collectionKey = getCollectionKey();
		
		return AbstractFutureResult.getBooleanInFuture(this.getCache(), new IJedisFutureGetCommand<Boolean>(){
			@Override
			public Response<Boolean> doInTransaction(Transaction transaction) {
				
				return transaction.sismember(collectionKey, ByteUtils.serializeToString(t));
			}
		});
	}

	

	@Override
	public IFutureResult<List<T>> getAllInFuture() {
		
		final String collectionKey = getCollectionKey();
		
		return AbstractFutureResult.getListInFuture(this.getCache(), new IJedisFutureGetCommand<? extends Collection<String>>(){
			@Override
			public Response<? extends Collection<String>> doInTransaction(Transaction transaction) {
				
				return transaction.smembers(collectionKey);
			}
		});
		
	}

	@Override
	public IFutureResult<Integer> sizeInFuture() {
		
		final String collectionKey = getCollectionKey();
		
		return AbstractFutureResult.getIntegerInFuture(this.getCache(), new IJedisFutureGetCommand<Long>(){
			@Override
			public Response<Long> doInTransaction(Transaction transaction) {
				
				return transaction.scard(collectionKey);
			}
		});
		
	}

}
