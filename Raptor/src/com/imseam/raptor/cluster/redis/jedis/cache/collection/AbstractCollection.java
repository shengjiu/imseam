package com.imseam.raptor.cluster.redis.jedis.cache.collection;

import redis.clients.jedis.Jedis;

import com.imseam.raptor.cluster.redis.jedis.cache.JedisClusterCache;

public class AbstractCollection {

	private String collectionKey;
	private JedisClusterCache cache;
		
	protected  AbstractCollection(JedisClusterCache cache, String collectionKey){
		this.collectionKey = collectionKey;
		this.cache = cache;
	}

	public JedisClusterCache getCache() {
		return cache;
	}
	
	protected Jedis getJedis(){
		return cache.getJedis();
	}

	public String getCollectionKey() {
		return collectionKey;
	}
}
