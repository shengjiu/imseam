package com.imseam.jms.dto;

import java.io.Serializable;

public class OperatorChangeDTO implements Serializable{
	
	private String userLoginEmail;
	
	private long operatorOid;
	
	private String stationId;
	
	private long stationOid;
	
	private String applicationStatus;
	
	private String operatorStatus;
	
	private long operatorNumber;
	
	private String firstName;
	
	private String lastName;
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public long getOperatorNumber() {
		return operatorNumber;
	}

	public void setOperatorNumber(long operatorNumber) {
		this.operatorNumber = operatorNumber;
	}

	public OperatorChangeDTO(){
		
	}

	public long getOperatorOid() {
		return operatorOid;
	}

	public void setOperatorOid(long operatorOid) {
		this.operatorOid = operatorOid;
	}

	public String getStationId() {
		return stationId;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}

	public long getStationOid() {
		return stationOid;
	}

	public void setStationOid(long stationOid) {
		this.stationOid = stationOid;
	}



	public String getApplicationStatus() {
		return applicationStatus;
	}

	public void setApplicationStatus(String applicationStatus) {
		this.applicationStatus = applicationStatus;
	}

	public String getOperatorStatus() {
		return operatorStatus;
	}

	public void setOperatorStatus(String operatorStatus) {
		this.operatorStatus = operatorStatus;
	}

	public String getUserLoginEmail() {
		return userLoginEmail;
	}

	public void setUserLoginEmail(String userLoginEmail) {
		this.userLoginEmail = userLoginEmail;
	}

}
