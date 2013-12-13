package com.imseam.raptor.cluster.redis.jedis.cache.commands;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import com.imseam.raptor.cluster.redis.jedis.cache.AbstractUpdateCommand;
import com.imseam.raptor.cluster.redis.jedis.cache.ByteUtils;

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
		jedisTransaction.set(ByteUtils.toBytes(key), ByteUtils.serialize(value));
		
	}


	@Override
	public Object doCommandWithoutTransaction(Jedis jedis) {
		
		jedis.set(ByteUtils.toBytes(key), ByteUtils.serialize(value));
		return null;
	}

	
}
