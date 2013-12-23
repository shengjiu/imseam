package com.imseam.raptor.cluster.redis.jedis.cache;


public class JedisActiveLocks {
	
	private String[] lockedKeys;
	
	public JedisActiveLocks(String ...lockedKeys){
		this.lockedKeys = lockedKeys;
	}
	
	public void unlock(){
		
	}

}
