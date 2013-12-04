package com.imseam.raptor.cluster.redis.jedis.cache;

import redis.clients.jedis.Jedis;

import com.imseam.cluster.IClusterCache;
import com.imseam.cluster.IClusterCachePool;
import com.imseam.raptor.cluster.redis.jedis.JedisInstance;

public class JedisCachePool implements IClusterCachePool {
	
	private ThreadLocal<JedisClusterCache> jedisCacheThreadLocal = new ThreadLocal<JedisClusterCache>();
	
	@Override
	public IClusterCache checkout() {
		
		JedisClusterCache cache = jedisCacheThreadLocal.get();
		if(cache != null){
			//if the thread is not as expected
			//ExceptionUtil.createRuntimeException("The JedisCache is already created in the Thread");
			return cache;
		}
		
		 cache = new JedisClusterCache(JedisInstance.getJedisFromPool(), this);
		jedisCacheThreadLocal.set(cache);
		return cache;
	}
	
	void releaseToPool(Jedis jedis){
		JedisInstance.returnToPool(jedis);
		jedisCacheThreadLocal.remove();
	}

}
