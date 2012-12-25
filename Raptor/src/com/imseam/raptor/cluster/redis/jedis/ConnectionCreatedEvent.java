package com.imseam.raptor.cluster.redis.jedis;

public class ConnectionCreatedEvent implements JedisGlobalEvent {

	private static final long serialVersionUID = 4155897861390549216L;
	private String eventConnectionUid = null;
	private String eventLocalServerUid = null;
	
	public ConnectionCreatedEvent(String eventConnectionUid, String eventLocalServerUid){
		this.eventConnectionUid = eventConnectionUid;
		this.eventLocalServerUid = eventLocalServerUid;
	}

	@Override
	public void processEvent() {
		ConnectionUidLocalCache.setServerUid(eventConnectionUid, eventLocalServerUid);
	}

}
