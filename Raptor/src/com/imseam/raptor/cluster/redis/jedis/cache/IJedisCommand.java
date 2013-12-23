package com.imseam.raptor.cluster.redis.jedis.cache;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

public interface IJedisCommand<T>{

	T doImmediate(Jedis jedis);
	
	void doInTransaction(Transaction transaction);
	
}
