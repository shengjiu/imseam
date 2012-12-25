package com.imseam.raptor.standard;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IBuddy;
import com.imseam.chatlet.exception.SessionExpiredException;
import com.imseam.chatlet.listener.event.SessionEvent;
import com.imseam.raptor.IChatletApplication;
import com.imseam.raptor.IEventListenerManager;
import com.imseam.raptor.ISessionManager;
import com.imseam.raptor.chatlet.EventTypeEnum;
import com.imseam.raptor.chatlet.MessengerBuddy;
import com.imseam.raptor.chatlet.UserSession;

public class UserSessionManager implements ISessionManager {
	
	private static Log log = LogFactory.getLog(UserSessionManager.class);
	
	private ConcurrentHashMap<String, UserSession> sessionMap = new ConcurrentHashMap<String, UserSession>();
	
	private IEventListenerManager eventListenermanager = null;
	
	public UserSessionManager(){
		
	}
	
	@Override
	public void initApplication(IChatletApplication application) {
		eventListenermanager = application.getEventListenerManager();
	}

	
	@Override
	public UserSession findUserSession(String buddyUid) {
		return sessionMap.get( buddyUid);
	}
	

	@Override
	public UserSession createUserSession(IBuddy buddy) {
		
		UserSession managedSession = this.sessionMap.get(buddy.getUid());

		try{
			
			if(managedSession != null){
				managedSession.access();
				log.debug(String.format("The user(%s) session is existing",
						buddy));
				return managedSession;
			}
		}
		catch (SessionExpiredException sexp) {
			log.warn(String.format(
					"The user(%s) session is existing, but expired",
					buddy));
		}
		
		return newSession(buddy);
	}
	
	private UserSession newSession(IBuddy buddy){
		MessengerBuddy user = (MessengerBuddy)buddy;
		UserSession newSession = new UserSession(user);
		UserSession oldSession = sessionMap.putIfAbsent(user.getUid(), newSession);
		if(oldSession == null){
			eventListenermanager.fireEvent(EventTypeEnum.SessionStarted, new SessionEvent(this, newSession));
		}else{
			try{
				oldSession.access();
				newSession = oldSession;
			
				}catch(SessionExpiredException sexp) {
					log.warn(String.format(
							"The user(%s:%s) session is existing, but expired",
							user.getServiceId(), user.getUid()));
					try{
						log.warn("The creating-session thread is waiting for destorying an expired session: " + user.getUid());
						Thread.sleep(100);
					}catch(Exception exp){
						log.error(exp);
					}
					newSession = newSession(user);
				}
			}
		return newSession;
	}
	
	private boolean threadDone = false;

	private int backgroundProcessorDelay = 60;

	private boolean started = false;

	private Thread backGroundProcessor = null;

	private String backGroundProcessorName = "Session Manager background processor";

	public void start() {
		if (started) {
			log.warn("The session manager background thread has been started");
			return;
		}

		started = true;
		backGroundProcessor = new Thread(new BackgroundProcessor(),
				backGroundProcessorName);
		backGroundProcessor.setDaemon(true);
		backGroundProcessor.start();
	}

	public void stop() {
		if (!started) {
			log.warn("The session manager background thread is NOT started");
			return;
		}

		if (backGroundProcessor == null)
			return;

		threadDone = true;
		backGroundProcessor.interrupt();
		try {
			backGroundProcessor.join();
		} catch (InterruptedException e) {
			;
		}
		started = false;
		backGroundProcessor = null;
	}

	private class BackgroundProcessor implements Runnable {

		public void run() {
			log.warn("The session manager background thread has been started");
			while (!threadDone) {
				try {
					Thread.sleep(backgroundProcessorDelay * 1000L);
				} catch (InterruptedException e) {
					;
				}
				if (!threadDone) {
					processExpires();
				}
			}
		}
	}

	/**
	 * Invalidate all sessions that have expired.
	 */
	public void processExpires() {

		long timeNow = System.currentTimeMillis();

		int expireHere = 0;

		for (UserSession session : sessionMap.values()) {
			if (session.checkSessionTimeOut()) {
				expireHere++;
				try{
					eventListenermanager.fireEvent(EventTypeEnum.SessionStopped, new SessionEvent(this, session));
				}catch(Exception exp){
					log.warn("Exception happened when trying to end user session!");
				}finally{
					sessionMap.remove(session.getMessengerUser().getUid(), session);
				}
			}
		}
		long timeEnd = System.currentTimeMillis();
		log.debug("End expire sessions: " + " processing time is: "
						+ (timeEnd - timeNow) + ", and expired sessions: "
						+ expireHere);
	}


	

}
