package com.imseam.raptor.chatlet;

import java.util.UUID;


public abstract class UidHelper {
	
	private static final String localServerId = "server0-"; //UUID.randomUUID().toString();
	private static final char localServerIdLength = (char)(localServerId.length() + 1);
	
	
	public static String getLocalServerId(){
//		if(localServerId == null){
//			localServerId = UUID.randomUUID().toString();
//			localServerIdLength = (char)(localServerId.length() + 1);
//		}
		return localServerId;
	}

	private static char getLocalServerIdLength(){
//		if(localServerId == null){
//			localServerId = UUID.randomUUID().toString();
//			localServerIdLength = (char)(localServerId.length() + 1);
//		}
		return localServerIdLength;
	}

	public static String constructServerWiseUniqueId(String uid){
		return getLocalServerIdLength() + getLocalServerId() + uid;
	}
	
	public static String parseLocalServerId(String anyServerWiseUid){
		return anyServerWiseUid.substring(1, anyServerWiseUid.charAt(0));
	}
	
	public static String createNewMeetingUid(){
		
		return constructServerWiseUniqueId(UUID.randomUUID().toString());
	}
	
	public static String constructConnectionUid(String serviceId, String hostUserId){
		char serviceIdLength = (char)(serviceId.length() + 1);
		return serviceIdLength + serviceId + hostUserId;
		
	}

	public static String constructBuddyUid(String connectionUid, String userId){
		char connectionUidLength = (char)(connectionUid.length() + 1);
		return connectionUidLength + connectionUid + userId;
	}
	
	public static String parseConnectionUidFromBuddyUid(String buddyUid){
		assert(buddyUid != null);
		return buddyUid.substring(1, buddyUid.charAt(0));
	}
	
	public static String parseUseridFromBuddyUid(String buddyUid){
		assert(buddyUid != null);
		return buddyUid.substring(buddyUid.charAt(0));	
	}

	public static String parseHostUseridFromBuddyUid(String buddyUid){
		assert(buddyUid != null);
		String connectionUid = parseConnectionUidFromBuddyUid(buddyUid); 
		return connectionUid.substring(connectionUid.charAt(0));
	}
	
	public static String parseServiceIdFromBuddyUid(String buddyUid){
		assert(buddyUid != null);
		String connectionUid = parseConnectionUidFromBuddyUid(buddyUid); 
		return connectionUid.substring(1, connectionUid.charAt(0));	
	}
	
	public static String parseHostUseridFromConnectionUid(String connectionUid){
		assert(connectionUid != null);
		return connectionUid.substring(connectionUid.charAt(0));
	}
	
	public static String parseServiceIdFromConnectionUid(String connectionUid){
		assert(connectionUid != null);
		return connectionUid.substring(1, connectionUid.charAt(0));
	}

	
	public static void main(String args[]){
		String serviceId = "msn";
		String hostUserId = "imseam@msn.com";
		String userId = "testuser@gmail.com";
		String connectionUid = constructConnectionUid(serviceId, hostUserId);
		String buddyUid = constructBuddyUid(connectionUid, userId);
		
		System.out.println(parseConnectionUidFromBuddyUid(buddyUid));
		System.out.println(parseUseridFromBuddyUid(buddyUid));
		System.out.println(parseHostUseridFromBuddyUid(buddyUid));
		System.out.println(parseServiceIdFromBuddyUid(buddyUid));
		System.out.println(parseHostUseridFromConnectionUid(connectionUid));
		System.out.println(parseServiceIdFromConnectionUid(connectionUid));
		
	}


	
}
