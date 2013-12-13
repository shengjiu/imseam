package com.imseam.raptor.cluster.redis.jedis.cache.commands;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import com.imseam.raptor.cluster.redis.jedis.cache.AbstractUpdateCommand;

public class Remove extends AbstractUpdateCommand {
	
	private String[] keys;

	Remove(String... keys) {
		super(keys);
		this.keys = keys;
	}
	
	public static Remove at(String...keys){
		return new Remove(keys);
	}

	@Override
	public void doCommandWithTransaction(Transaction jedisTransaction) {
		jedisTransaction.del(keys);
	}

	@Override
	public Object doCommandWithoutTransaction(Jedis jedis) {
		jedis.del(keys);
		return null;
	}

}
