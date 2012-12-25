package com.imseam.jms.dto;

import java.io.Serializable;

public class StationChangeDTO implements Serializable{
	

	private String stationId;
	
	private long stationOid;
	
	private String status;
	
	public StationChangeDTO(){
		
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
