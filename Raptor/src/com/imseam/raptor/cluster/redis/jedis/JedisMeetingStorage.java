package com.imseam.raptor.cluster.redis.jedis;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.util.SafeEncoder;

import com.imseam.chatlet.IMeeting;
import com.imseam.raptor.IChatletApplication;
import com.imseam.raptor.chatlet.MeetingContext;
import com.imseam.raptor.chatlet.UidHelper;
import com.imseam.raptor.cluster.IMeetingStorage;
import com.imseam.serialize.SerializerUtil;

public class JedisMeetingStorage implements IMeetingStorage {

	
	private IChatletApplication application = null;
	
	@Override
	public void initApplication(IChatletApplication application) {
		JedisInstance.initialize(application);
		this.application = application;

	}
	
	@Override
	public IMeeting createMeeting(String... windowUids) {
		String meetingUid = UidHelper.createNewMeetingUid();
		MeetingContext meeting = new MeetingContext(application, meetingUid);
		
		Jedis jedis = null;
		try{
			jedis = JedisInstance.getJedisFromPool();
			Pipeline pipeline = jedis.pipelined();
			pipeline.multi();
			if(windowUids != null){
				for(String windowUid : windowUids){
					pipeline.sadd(meetingUid, windowUid);
				}
			}
			pipeline.exec();
			pipeline.sync();
		}finally{
			JedisInstance.returnToPool(jedis);
		}
		
		return meeting;	
	}
	
	private byte [] getMeetingObjectMapKey(String meetingUid){
		return SafeEncoder.encode(meetingUid +"_object_map");
	}

	
	@Override
	public IMeeting getExistingMeeting(String meetingUid) {
		Jedis jedis = null;
		try{
			jedis = JedisInstance.getJedisFromPool();
			Set<String> windowUidSet = jedis.smembers(meetingUid);	
			if(windowUidSet != null && windowUidSet.size() > 0){
				return new MeetingContext(application, meetingUid);
			}
		}finally{
			JedisInstance.returnToPool(jedis);
		}
		return null;
	}

	@Override
	public void destoryMeeting(String meetingUid) {
		Jedis jedis = null;
		try{
			jedis = JedisInstance.getJedisFromPool();
			Pipeline pipeline = jedis.pipelined();
			pipeline.multi();			
			pipeline.del(meetingUid);
			pipeline.del(this.getMeetingObjectMapKey(meetingUid));
			pipeline.exec();
		}finally{
			JedisInstance.returnToPool(jedis);
		}	
	}

	@Override
	public Object put(String meetingUid, String key, Object obj) {
		byte[] meetingObjectHashMapKey = this.getMeetingObjectMapKey(meetingUid);
		byte[] keyBytes = SafeEncoder.encode(key);
		byte[] originalBytes = null;
		Object originalObject = null;
		Jedis jedis = null;
		try{
			jedis = JedisInstance.getJedisFromPool();
			originalBytes = jedis.hget(meetingObjectHashMapKey, keyBytes);
			jedis.hset(meetingObjectHashMapKey, keyBytes, SerializerUtil.serialize(obj));
			if(originalBytes != null){
				originalObject = SerializerUtil.deserialize(originalBytes);
			}
		}finally{
			JedisInstance.returnToPool(jedis);
		}	
		return originalObject;
	}

	@Override
	public Object remove(String meetingUid, String key) {
		byte[] meetingObjectHashMapKey = this.getMeetingObjectMapKey(meetingUid);
		byte[] keyBytes = SafeEncoder.encode(key);
		byte[] originalBytes = null;
		Object originalObject = null;
		Jedis jedis = null;
		try{
			jedis = JedisInstance.getJedisFromPool();
			originalBytes = jedis.hget(meetingObjectHashMapKey, keyBytes);
			jedis.hdel(meetingObjectHashMapKey, keyBytes);
			if(originalBytes != null){
				originalObject = SerializerUtil.deserialize(originalBytes);
			}
		}finally{
			JedisInstance.returnToPool(jedis);
		}	
		return originalObject;	
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String meetingUid, String key) {
		byte[] meetingObjectHashMapKey = this.getMeetingObjectMapKey(meetingUid);
		byte[] keyBytes = SafeEncoder.encode(key);
		byte[] originalBytes = null;
		T originalObject = null;
		Jedis jedis = null;
		try{
			jedis = JedisInstance.getJedisFromPool();
			originalBytes = jedis.hget(meetingObjectHashMapKey, keyBytes);
			if(originalBytes != null){
				originalObject = (T)SerializerUtil.deserialize(originalBytes);
			}
		}finally{
			JedisInstance.returnToPool(jedis);
		}	
		return originalObject;	
	}

	@Override
	public Set<String> getReadOnlyWindowUidSet(String meetingUid) {
		Set<String> meetingWindowUidSet = null;
		Jedis jedis = null;
		try{
			jedis = JedisInstance.getJedisFromPool();
			meetingWindowUidSet = jedis.smembers(meetingUid);
			if(meetingWindowUidSet == null){
				return null;
			}
		}finally{
			JedisInstance.returnToPool(jedis);
		}	
		return Collections.unmodifiableSet(meetingWindowUidSet);
	}

	@Override
	public void addWindowsToMeeting(String meetingUid, String... windowUids) {
		Jedis jedis = null;
		try{
			jedis = JedisInstance.getJedisFromPool();
			Pipeline pipeline = jedis.pipelined();
			pipeline.multi();
			for(String windowUid : windowUids){
				pipeline.sadd(meetingUid, windowUid);
			}
			pipeline.exec();
			pipeline.sync();
		}finally{
			JedisInstance.returnToPool(jedis);
		}	
	}

	@Override
	public void removeWindowsFromMeeting(String meetingUid,
			String... windowUids) {
		Jedis jedis = null;
		try{
			jedis = JedisInstance.getJedisFromPool();
			Pipeline pipeline = jedis.pipelined();
			pipeline.multi();
			for(String windowUid : windowUids){
				pipeline.srem(meetingUid, windowUid);
			}
			pipeline.exec();
			pipeline.sync();
		}finally{
			JedisInstance.returnToPool(jedis);
		}	
	}

	@Override
	public Set<String> getKeySet(String meetingUid) {
		byte[] meetingObjectHashMapKey = this.getMeetingObjectMapKey(meetingUid);
		
		Set<String> meetingObjectMapKeySet = null;
		
		Jedis jedis = null;
		try{
			jedis = JedisInstance.getJedisFromPool();
			Set<byte[]> meetingObjectMapBytesKeySet = jedis.hkeys(meetingObjectHashMapKey);
			
			if(meetingObjectMapBytesKeySet != null && meetingObjectMapBytesKeySet.size() > 0){
				meetingObjectMapKeySet = new HashSet<String>();
				for(byte[] bytesKey : meetingObjectMapBytesKeySet){
					meetingObjectMapKeySet.add(SafeEncoder.encode(bytesKey));
				}
			}
		}finally{
			JedisInstance.returnToPool(jedis);
		}	
		return meetingObjectMapKeySet;	
	}



}
