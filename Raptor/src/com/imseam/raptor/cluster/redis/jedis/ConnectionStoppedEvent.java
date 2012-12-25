package com.imseam.raptor.cluster.redis.jedis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConnectionStoppedEvent implements JedisGlobalEvent {
	
	private static Log log = LogFactory.getLog(ConnectionStoppedEvent.class);

	private static final long serialVersionUID = 5007632440314603892L;
	private String eventConnectionUid = null;
	private String eventLocalServerUid = null;
	
	public ConnectionStoppedEvent(String eventConnectionUid, String eventLocalServerUid){
		this.eventConnectionUid = eventConnectionUid;
		this.eventLocalServerUid = eventLocalServerUid;
	}

	@Override
	public void processEvent() {
		if(eventLocalServerUid.equals(ConnectionUidLocalCache.getServerUid(eventConnectionUid))){
			ConnectionUidLocalCache.removeServerUid(eventConnectionUid);
		}else{
			log.warn("The current local connnectionuid cache has a different serveruid, current:" + ConnectionUidLocalCache.getServerUid(eventConnectionUid) + "new serveruid: " + this.eventLocalServerUid);
		}
	}


}
