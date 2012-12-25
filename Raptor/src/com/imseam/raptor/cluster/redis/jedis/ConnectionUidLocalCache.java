package com.imseam.raptor.cluster.redis.jedis;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectionUidLocalCache {
	
	private static ConcurrentHashMap<String, String> connectionUidToServerMap = new ConcurrentHashMap<String, String>();
	
	public static String getServerUid(String connectionUid){
		return connectionUidToServerMap.get(connectionUid);
	}

	public static String setServerUid(String connectionUid, String serverUid){
		return connectionUidToServerMap.put(connectionUid, serverUid);
	}

	public static String removeServerUid(String connectionUid){
		return connectionUidToServerMap.remove(connectionUid);
	}

}
