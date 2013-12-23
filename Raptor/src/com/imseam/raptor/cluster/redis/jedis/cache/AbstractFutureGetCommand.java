package com.imseam.raptor.cluster.redis.jedis.cache;

import redis.clients.jedis.Jedis;

import com.imseam.common.util.ExceptionUtil;

public abstract class AbstractFutureGetCommand<T> implements IJedisCommand<T> {

	@Override
	final public T doImmediate(Jedis jedis) {
		ExceptionUtil.createRuntimeException("The future get command must be in a transaction");
		return null;
	}

}
