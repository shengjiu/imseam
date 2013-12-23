package com.imseam.raptor.cluster.redis.jedis.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import redis.clients.util.SafeEncoder;

import com.imseam.serialize.SerializerUtil;

public class ByteUtils {

	public static byte[][] toBytesArray(String ... strs){
		if(strs == null || strs.length == 0) return null;
		byte[][] results = new byte[strs.length][];
		for(int i=0; i < strs.length; i++){
			results[i] = toBytes(strs[i]);
		}
		return results;
	}
	
	public static <T> List<T> deserialize(Collection<byte[]> bytesList){
		if(bytesList == null) return null;
		List<T> resultList = new ArrayList();;
		for(byte[] bytes : bytesList){
			resultList.add((T)SerializerUtil.deserialize(bytes));		
		}
		return resultList;
	}
	
	public static <T> List<T> deserializeString(Collection<String> stringList){
		if(stringList == null) return null;
		List<T> resultList = new ArrayList<T>();
		for(String str : stringList){
			resultList.add((T)SerializerUtil.deserialize(str.getBytes()));		
		}
		return resultList;
	}

	public static <T> T deserializeString(String str){
		
		return (T)SerializerUtil.deserialize(str.getBytes());		
	}

	
	public static <T> T deserialize(byte[] bytes){
		return (T)SerializerUtil.deserialize(bytes);		
	}

	public static byte[] serialize(Object object){
		return SerializerUtil.serialize(object);		
	}
	
	public static byte[][] serialize(Object... objects){
		if(objects == null || objects.length == 0) return null;
		byte[][] results = new byte[objects.length][];
		for(int i = 0; i < objects.length; i++){
			results[i] = SerializerUtil.serialize(objects[i]);
		}
		return results;
	}
	
	public static <T> String[] serializeToStrings(T... objects){
		if(objects == null || objects.length == 0) return null;
		String[] results = new String[objects.length];
		for(int i = 0; i < objects.length; i++){
			results[i] = serializeToString(objects[i]);
		}
		return results;
	}

	public static <T> String serializeToString(T object){
		return new String(serialize(object));		
	}

	
	public static byte[] toBytes(String str){
		if(str == null) return null;
		return SafeEncoder.encode(str);
	}

}
