package com.imseam.raptor.cluster.redis.jedis;

import java.io.Serializable;

public interface JedisGlobalEvent extends Serializable{

	void processEvent();
	
}
