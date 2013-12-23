package com.imseam.raptor.cluster.redis.jedis.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

import com.imseam.cluster.IFutureResult;

public abstract class AbstractFutureResult<T> implements IFutureResult<T> {
	
	private boolean transactionCommitted = false;
	private boolean resultBuilt = false;
	private T result;
	
	public void transactionCommitted(){
		transactionCommitted = true;
	}

	public T get()throws ResultIsNotReadyException{
		if(!transactionCommitted){
			throw new ResultIsNotReadyException(); 
		}
		if(!resultBuilt){
			result = buildResult();
			resultBuilt = true;
		}
		return result;
	}
	
	public abstract T buildResult();
	
	
	public static IFutureResult<Integer> getIntegerInFuture(final JedisClusterCache cache, final IJedisCommand<Response<Long>> command) {
		final List<Response<Long>> responseList = new ArrayList<Response<Long>>();

		final AbstractFutureResult<Integer> futureResult = new AbstractFutureResult<Integer>(){
			@Override
			public Integer buildResult(){
				return responseList.get(0).get().intValue();
			}
		};

		cache.doFutureGetCommand(new AbstractFutureGetCommand<Response<Long>>(){
			@Override
			public Response<Long> doInTransaction(Transaction transaction) {
				futureResult.transactionCommitted();
				Response<Long> response = command.doInTransaction(transaction); 
				responseList.add(response);
				return response;
			}
		});
		
		return futureResult;	
	}

	public static IFutureResult<Boolean> getBooleanInFuture(final JedisClusterCache cache, final IJedisCommand<Response<Boolean>> command) {
		final List<Response<Boolean>> responseList = new ArrayList<Response<Boolean>>();

		final AbstractFutureResult<Boolean> futureResult = new AbstractFutureResult<Boolean>(){
			@Override
			public Boolean buildResult(){
				return responseList.get(0).get();
			}
		};

		cache.doFutureGetCommand(new AbstractFutureGetCommand<Response<Boolean>>(){
			@Override
			public Response<Boolean> doInTransaction(Transaction transaction) {
				futureResult.transactionCommitted();
				Response<Boolean> response = command.doInTransaction(transaction); 
				responseList.add(response);
				return response;
			}
		});
		
		return futureResult;	
	}
	
	public static <T> IFutureResult<List<T>> getListInFuture(final JedisClusterCache cache, final IJedisCommand<Response<? extends Collection<String>>> command) {
		final List<Response<? extends Collection<String>>> responseList = new ArrayList<Response<? extends Collection<String>>>();
		
		final AbstractFutureResult<List<T>> futureResult = new AbstractFutureResult<List<T>>(){
			@Override
			public List<T> buildResult(){
				List<T> result = ByteUtils.deserializeString(responseList.get(0).get());
				return result;
			}
		};
		
		cache.doFutureGetCommand(new AbstractFutureGetCommand<Response<? extends Collection<String>>>(){
			@Override
			public Response<? extends Collection<String>> doInTransaction(Transaction transaction) {
				futureResult.transactionCommitted();
				Response<? extends Collection<String>> response = command.doInTransaction(transaction); 
				responseList.add(response);
				return response;
			}
		});
		
		return futureResult;
	}

	public static <T> IFutureResult<Map<String, T>> getMapInFuture(final JedisClusterCache cache, final IJedisCommand<Response<Map<String, String>>> command) {
		final List<Response<Map<String, String>>> responseList = new ArrayList<Response<Map<String, String>>>();
		final AbstractFutureResult<Map<String, T>> futureResult = new AbstractFutureResult<Map<String, T>>(){
			@Override
			public Map<String, T> buildResult(){
				Map<String, String> stringMap = responseList.get(0).get();
				if(stringMap == null || stringMap.size() == 0) return null;
				Map<String, T> tMap = new HashMap<String, T>();
				for(String key : stringMap.keySet()){
					tMap.put(key, ByteUtils.<T>deserializeString(stringMap.get(key)));
				}
				return tMap;
			}
		};

		cache.doFutureGetCommand(new AbstractFutureGetCommand<Response<Map<String, String>>>(){
			@Override
			public Response<Map<String, String>> doInTransaction(Transaction transaction) {
				futureResult.transactionCommitted();
				Response<Map<String, String>> response = command.doInTransaction(transaction);
				responseList.add(response);
				return response;
			}
		});
		return futureResult;

	}
	
	public static <T> IFutureResult<T> getObjectInFuture(final JedisClusterCache cache, final IJedisCommand<Response<String>> command) {

		final List<Response<String>> responseList = new ArrayList<Response<String>>();
		
		final AbstractFutureResult<T> futureResult = new AbstractFutureResult<T>(){
			@Override
			public T buildResult(){
				return ByteUtils.deserializeString(responseList.get(0).get());
			}
		};

		cache.doFutureGetCommand(new AbstractFutureGetCommand<Response<String>>(){
			@Override
			public Response<String> doInTransaction(Transaction transaction) {
				futureResult.transactionCommitted();
				Response<String> response = command.doInTransaction(transaction); 
				responseList.add(response);
				return response;
			}
		});
		
		return futureResult;
	}
	
}
