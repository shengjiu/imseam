package com.imseam.raptor.cluster.redis.jedis.cache;

import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

public interface IJedisFutureGetCommand<W>{

	Response<? extends W> doInTransaction(Transaction transaction);
	
}
