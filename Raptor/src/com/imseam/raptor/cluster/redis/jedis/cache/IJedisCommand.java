package com.imseam.raptor.cluster.redis.jedis.cache;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

public interface IJedisCommand<T, W>{

	T doImmediate(Jedis jedis);
	
	Response<W> doInTransaction(Transaction transaction);
	
}
