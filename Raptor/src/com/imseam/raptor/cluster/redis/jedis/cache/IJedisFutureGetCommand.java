package com.imseam.raptor.cluster.redis.jedis.cache;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

public interface IJedisFutureGetCommand<W>{

	Response<W> doInTransaction(Transaction transaction);
	
}
