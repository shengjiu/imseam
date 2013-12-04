package com.imseam.raptor.cluster.redis.jedis.cache.commands;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.util.SafeEncoder;

import com.imseam.raptor.cluster.redis.jedis.cache.AbstractUpdateCommand;

public class Remove extends AbstractUpdateCommand {
	
	private String key;

	Remove(String key) {
		super(key);
		this.key = key;
	}
	
	public static Remove at(String key){
		return new Remove(key);
	}

	@Override
	public void doCommandWithTransaction(Transaction jedisTransaction) {
		jedisTransaction.del(SafeEncoder.encode(key));
	}

	@Override
	public void doCommandWithoutTransaction(Jedis jedis) {
		jedis.del(SafeEncoder.encode(key));
	}

}
