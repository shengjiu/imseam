package com.imseam.raptor.cluster.redis.jedis.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

import com.imseam.cluster.IFutureResult;

public abstract class AbstractFutureResult<T, W> implements IFutureResult<T> {
	
	private boolean transactionCommitted = false;
	private boolean resultBuilt = false;
	private T result;
	private Response<W> jedisResponse = null;
	
	public void transactionCommitted(Response<W> jedisResponse){
		transactionCommitted = true;
		this.jedisResponse = jedisResponse;
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
	
	
	public static IFutureResult<Integer> getIntegerInFuture(final JedisClusterCache cache, final IJedisFutureGetCommand<Long> command) {

        final AbstractFutureResult<Integer, Long> futureResult = new AbstractFutureResult<Integer, Long>(){
                @Override
                public Integer buildResult(){
                	if(jedisResponse != null){
                        	return jedisResponse.get().intValue();
                	}
                	return null;
                }
        };

        addFutureGetCommand(cache, getCommand, futureResult)
        
        return futureResult;        
	}

	public static IFutureResult<Boolean> getBooleanInFuture(final JedisClusterCache cache, final IJedisFutureGetCommand<Boolean> command) {
		

		final AbstractFutureResult<Boolean, Boolean> futureResult = new AbstractFutureResult<Boolean, Boolean>(){
			@Override
			public Boolean buildResult(){
			if(jedisResponse != null){
                        	return jedisResponse.get();
                	}				
			return null;
			
		};

		addFutureGetCommand(cache, getCommand, futureResult)
		
		return futureResult;	
	}
	
	public static <T> IFutureResult<List<T>> getListInFuture(final JedisClusterCache cache, final IJedisFutureGetCommand<? extends Collection<String>> command) {
		final AbstractFutureResult<List<T>, ? extends Collection<String>> futureResult = new AbstractFutureResult<List<T>, ? extends Collection<String>>(){
			@Override
			public List<T> buildResult(){
			if(jedisResponse != null){
                        	return ByteUtils.deserializeString(jedisResponse.get());
				
                	}				
			return null;
			
		};

		addFutureGetCommand(cache, getCommand, futureResult)
		
		return futureResult;		
		
	}

	public static <T> IFutureResult<Map<String, T>> getMapInFuture(final JedisClusterCache cache, final IJedisFutureGetCommand<Map<String, String>> command) {

		
		final AbstractFutureResult<Map<String, T>, Map<String, String>> futureResult = new AbstractFutureResult<Map<String, T>, Map<String, String>>(){
			@Override
			public Map<String, T> buildResult(){
			if(jedisResponse != null){
                        	Map<String, String> stringMap = jedisResponse.get();
				if(stringMap == null || stringMap.size() == 0) return null;
				Map<String, T> tMap = new HashMap<String, T>();
				for(String key : stringMap.keySet()){
					tMap.put(key, ByteUtils.<T>deserializeString(stringMap.get(key)));
				}
				return tMap;
				
                	}				
			return null;
			
		};

		addFutureGetCommand(cache, getCommand, futureResult)
		
		return futureResult;

	}
	
	public static <T> IFutureResult<T> getObjectInFuture(final JedisClusterCache cache, final IJedisFutureGetCommand<String> command) {


		
        final AbstractFutureResult<T, String> futureResult = new AbstractFutureResult<T, String>(){
                @Override
                public T buildResult(){
                	if(jedisResponse != null){
                        	return ByteUtils.deserializeString(jedisResponse.get());
                	}
                	return null;
                }
        };

        addFutureGetCommand(cache, getCommand, futureResult)
        
        return futureResult;        
		
	}
	
	

	public static <W>void addFutureGetCommand(final JedisClusterCache cache,final IJedisFutureGetCommand<W> getCommand, final AbstractFutureResult<?, W> futureResult) {

        cache.doFutureGetCommand(new AbstractFutureGetCommand<?, W>(){
                @Override
                public void doInTransaction(Transaction transaction) {
                        Response<W> response = getCommand.doInTransaction(transaction);
                        futureResult.transactionCommitted(response);
                }
        });
        
        return futureResult;        
	}
	
}
