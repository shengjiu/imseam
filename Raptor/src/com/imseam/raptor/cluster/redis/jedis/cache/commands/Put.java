package com.imseam.raptor.cluster.redis.jedis.cache.commands;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.util.SafeEncoder;

import com.imseam.raptor.cluster.redis.jedis.cache.AbstractUpdateCommand;
import com.imseam.serialize.SerializerUtil;

public class Put <T> extends AbstractUpdateCommand{
	
	private T value;
	private String key;

	private Put(String key, T value) {
		super(key);
		this.value = value;
		
	}
	
	public static <T> Put<T> at(String key, T value){
		return new Put<T>(key, value);
	}

	@Override
	public void doCommandWithTransaction(Transaction jedisTransaction) {
		byte[] keyBytes = SafeEncoder.encode(key);
		jedisTransaction.set(keyBytes, SerializerUtil.serialize(value));
		
	}


	@Override
	public void doCommandWithoutTransaction(Jedis jedis) {
		
		byte[] keyBytes = SafeEncoder.encode(key);
		
		jedis.set(keyBytes, SerializerUtil.serialize(value));
			
	}

	
}
