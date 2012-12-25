package com.imseam.raptor.chatlet;

import com.imseam.chatlet.BuddyStatusEnum;
import com.imseam.chatlet.IBuddy;
import com.imseam.common.util.StringUtil;

public class MessengerBuddy implements IBuddy {
	
//	private static Log log = LogFactory.getLog(MessengerUser.class);
	private final String userId;
	private final String serviceId;
	private final String connectionUid;
	private final String uid;
	private BuddyStatusEnum status = null;
	
	
	public MessengerBuddy(String userId, String serviceId, String connectionUid){
		assert(!StringUtil.isNullOrEmptyAfterTrim(userId));
		
		this.userId = userId;
		this.serviceId = serviceId;
		this.connectionUid = connectionUid;
		this.uid = UidHelper.constructBuddyUid(connectionUid, userId);
	}
	
	public String getConnectionUid() {
		return connectionUid;
	}

	public String getServiceId() {
		return this.serviceId;
	}

	public String getUid() {
		return uid;
	}


	@Override
	public boolean equals(Object obj) {
		if(super.equals(obj)){
			return true;
		}
		MessengerBuddy compare = (MessengerBuddy)obj;
		if(uid.equals(compare.getUid()))
			return true;
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return uid + "(" + status + ")";
	}

	@Override
	public BuddyStatusEnum getBuddyStatus() {
		return null;
	}
	
	public void setBuddyStatus(BuddyStatusEnum status){
		this.status = status;
	}

	@Override
	public String getUserId() {
		return this.userId;
	}

	@Override
	public UidType getUidType() {
		return UidType.BUDDY;
	}


}
