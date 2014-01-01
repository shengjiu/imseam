package com.imseam.freamworktest.meeting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;

import com.imseam.cdi.chatlet.ext.annotation.BuddySignIn;
import com.imseam.cdi.chatlet.ext.annotation.WindowStarted;
import com.imseam.cdi.context.IMRequestScoped;
import com.imseam.chatlet.IApplication;
import com.imseam.chatlet.listener.event.BuddyEvent;
import com.imseam.chatlet.listener.event.WindowEvent;
import com.imseam.cluster.IClusterCache;


@IMRequestScoped
public class SiteProxy {

	private Instance<IClusterCache> clusterCacheInstance;
	private Instance<IApplication> application;
	
	private String getSiteKey(String siteNumber){
		return "Site-" + siteNumber;
	}
	
	public void onBuddySignIn(@Observes @BuddySignIn BuddyEvent event){
		
		String userId = event.getBuddy().getUserId();
		//sitenumber-usernumber-operator waitsiteuser
		
		if(userId.endsWith("waitsiteuser")){
			addUserToSite(userId, null);
		}
	}
	
	public void onWindowInitialized(@Observes @WindowStarted WindowEvent event){
		String userId = event.getWindow().getDefaultChannel().getBuddy().getUserId();
		if(!userId.endsWith("waitsiteuser")){
			addUserToSite(userId, event.getWindow().getUid());
		}
	}
	
	private void addUserToSite(String userId, String windowUid){
		boolean isOperator = false;
		boolean isWaitSiteUser = false;
		if(userId.endsWith("operator")){
			isOperator = true;
		}
		if(userId.endsWith("waitsiteuser")){
			isWaitSiteUser = true;
		}
		String[] splittedStrings = userId.split("-");
		String siteNumber = splittedStrings[0];
		String siteKey = getSiteKey(siteNumber);
		int siteTotalUserNumber = Integer.parseInt(splittedStrings[1]);
//		String userNumber = splittedStrings[1];
		
		
		IClusterCache cache = clusterCacheInstance.get();
		
		Site site = cache.get(siteKey);
		
		if(site == null){
			site = new Site();
			site.siteNumber = siteNumber;
		}
		
		if(isOperator){
			site.operatorWindowList.add(windowUid);
		}else if(isWaitSiteUser){
			site.waitSiteUserList.add(userId);
		}else{
			site.clientWindowList.add(windowUid);
		}

		site.lastModifiedDate = new Date();
		site.onlineUserNumber++;

		Site existingSite = cache.putIfAbsent(siteKey, site);
		if(existingSite != null){
			
		}
		
		if(site.onlineUserNumber == siteTotalUserNumber){
			
		}
		
	}
	
	static public class Site implements Serializable{
		private String siteNumber;
		private List<String> operatorWindowList = new ArrayList<String>();
		private List<String> clientWindowList = new ArrayList<String>();
		private List<String> waitSiteUserList = new ArrayList<String>();
		private int onlineUserNumber = 0;
		private Date lastModifiedDate;
		
		
	}
}
