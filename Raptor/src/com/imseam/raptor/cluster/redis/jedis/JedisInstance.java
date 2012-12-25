package com.imseam.raptor.cluster.redis.jedis;

import com.imseam.raptor.IChatletApplication;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisInstance {
	
	private static JedisPool pool = null; 
	
	private static final String REDIS_HOST = "REDIS_HOST";
	private static final String REDIS_PORT = "REDIS_PORT";
	private static String host = null;
	private static int port = -1;
	
	public static void initialize(IChatletApplication application){
		host = application.getApplicationContext().getInitParam(REDIS_HOST);
		port = Integer.parseInt(application.getApplicationContext().getInitParam(REDIS_PORT));

		if(pool == null){
			pool = new JedisPool(new JedisPoolConfig(), host, port);
		}
	}
	
	public static Jedis getJedisFromPool(){
		return pool.getResource();
	}
	
	public static void returnToPool(Jedis jedis){
		pool.returnResource(jedis);
	}
	
	public static Jedis getStandaloneJedis(){
		Jedis jedis = new Jedis(host, port, 0);
		jedis.connect();
		
		return jedis;
	}
}
