package com.imseam.cdi.chatlet;

import java.io.Serializable;

import com.imseam.chatlet.IIdentiable.UidType;
import com.imseam.chatlet.IWindow;


public class Id implements Serializable {
	
	private static final long serialVersionUID = -8605849268498064787L;
	private String uid;
	private UidType uidType;
	
	private Id(String uid, UidType uidType){
		this.uid = uid;
		this.uidType = uidType;
	}
	
	public String getUid() {
		return uid;
	}

	public UidType getUidType() {
		return uidType;
	}
	
	public static Id windowUid(String windowUid){
		return new Id(windowUid, UidType.WINDOW);
	}

	public static Id channelUid(String channelUid){
		return new Id(channelUid, UidType.CHANNEL);
	}

	public static Id connectionUid(String connectionUid){
		return new Id(connectionUid, UidType.CONNECTION);
	}

	public static Id buddyUid(String buddyUid){
		return new Id(buddyUid, UidType.BUDDY);
	}

	public static Id meetingUid(String meetingUid){
		return new Id(meetingUid, UidType.MEETING);
	}

	public static Id sessionUid(String sessionUid){
		return new Id(sessionUid, UidType.SESSION);
	}
	
	public static Id eventUid(String eventUid){
		return new Id(eventUid, UidType.SYSTEMEVENT);
	}

	public static Id userRequestUid(String userRequestUid){
		return new Id(userRequestUid, UidType.USERREQUEST);
	}

	public static Id applicationUid(String applicationName){
		return new Id(applicationName, UidType.APPICATION);
	}
	
	public static Id of(IWindow window){
		return new Id(window.getUid(), UidType.WINDOW);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uid == null) ? 0 : uid.hashCode());
		result = prime * result + ((uidType == null) ? 0 : uidType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Id other = (Id) obj;
		if (uid == null) {
			if (other.uid != null)
				return false;
		} else if (!uid.equals(other.uid))
			return false;
		if (uidType != other.uidType)
			return false;
		return true;
	}
	
	

	@Override
	public String toString() {
		return this.uidType +": " + this.uid;
	}
	

	

	

}
