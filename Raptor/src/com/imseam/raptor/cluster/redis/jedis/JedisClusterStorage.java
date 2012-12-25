package com.imseam.raptor.cluster.redis.jedis;

import redis.clients.jedis.Jedis;
import redis.clients.util.SafeEncoder;

import com.imseam.raptor.IChatletApplication;
import com.imseam.raptor.cluster.IClusterStorage;
import com.imseam.serialize.SerializerUtil;

public class JedisClusterStorage implements IClusterStorage {

	@Override
	public void initApplication(IChatletApplication application) {
		JedisInstance.initialize(application);
	}

	@Override
	public <T> T put(String key, T obj) {
		byte[] originalBytes = null;
		T originalObject = null;
		byte[] keyBytes = SafeEncoder.encode(key);
		Jedis jedis = null;
		try{
			jedis = JedisInstance.getJedisFromPool();
			originalBytes = jedis.get(keyBytes);
			jedis.set(keyBytes, SerializerUtil.serialize(obj));
			
			if(originalBytes != null){
				originalObject = (T)SerializerUtil.deserialize(originalBytes);
			}
		}finally{
			JedisInstance.returnToPool(jedis);
		}
		return originalObject;
	}

	@Override
	public <T> T putIfAbsent(String key, T obj) {
		byte[] originalBytes = null;
		T originalObject = null;
		byte[] keyBytes = SafeEncoder.encode(key);
		Jedis jedis = null;
		try{
			jedis = JedisInstance.getJedisFromPool();
			originalBytes = jedis.get(keyBytes);
			
			if(originalBytes == null){
				jedis.set(keyBytes, SerializerUtil.serialize(obj));
			}else{
				originalObject = (T)SerializerUtil.deserialize(originalBytes);
			}
		}finally{
			JedisInstance.returnToPool(jedis);
		}
		return originalObject;
	}

	@Override
	public <T> T remove(String key) {
		byte[] originalBytes = null;
		T originalObject = null;
		byte[] keyBytes = SafeEncoder.encode(key);
		Jedis jedis = null;
		try{
			jedis = JedisInstance.getJedisFromPool();
			originalBytes = jedis.get(keyBytes);
			
			jedis.del(keyBytes);
	
			if(originalBytes != null){
				originalObject = (T)SerializerUtil.deserialize(originalBytes);
			}
		}finally{
			JedisInstance.returnToPool(jedis);
		}
		return originalObject;
	}

	@Override
	public <T> T get(String key) {
		byte[] originalBytes = null;
		T originalObject = null;
		byte[] keyBytes = SafeEncoder.encode(key);
		Jedis jedis = null;
		try{
			jedis = JedisInstance.getJedisFromPool();
			originalBytes = jedis.get(keyBytes);
	
			if(originalBytes != null){
				originalObject = (T)SerializerUtil.deserialize(originalBytes);
			}
		}finally{
			JedisInstance.returnToPool(jedis);
		}
		return originalObject;		
	}

	@Override
	public void addListner(Object listener) {
		// TODO Auto-generated method stub

	}


}
