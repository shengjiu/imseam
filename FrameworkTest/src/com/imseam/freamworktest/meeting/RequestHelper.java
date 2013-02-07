package com.imseam.freamworktest.meeting;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import com.imseam.cdi.context.IMWindowScoped;
import com.imseam.chatlet.IUserRequest;

@IMWindowScoped @Named("requestHelper")
public class RequestHelper {
	
	 
	private @Inject Instance<IUserRequest> request;
	
	
	public IUserRequest getRequest(){
		return request.get();
	}

}
