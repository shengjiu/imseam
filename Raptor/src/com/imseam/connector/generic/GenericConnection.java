package com.imseam.connector.generic;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.BuddyStatusEnum;
import com.imseam.chatlet.IChatletMessage;
import com.imseam.chatlet.IStartActiveWindowCallback;
import com.imseam.chatlet.config.ConnectionConfig;
import com.imseam.chatlet.exception.InviteBuddyException;
import com.imseam.chatlet.exception.StartActiveWindowException;
import com.imseam.common.util.ClassUtil;
import com.imseam.common.util.ExceptionUtil;
import com.imseam.raptor.IChatletApplication;
import com.imseam.raptor.IMessengerWindow;
import com.imseam.raptor.core.AbstractConnection;

public class GenericConnection extends AbstractConnection{

	private static Log log = LogFactory.getLog(GenericConnection.class);
	
	private static String ADAPTER_CLASS_PARAM_NAME = "adapter-class";

	private Map<String, GenericWindow> windowMap = new ConcurrentHashMap<String, GenericWindow>();
	
	IMessengerConnectionAdapter adapter = null;

	public GenericConnection(IChatletApplication application,
			ConnectionConfig config) {
		super(application, config);
	}

	public void initialize() {
		
		String className = config.get(ADAPTER_CLASS_PARAM_NAME);
		
		try{
			adapter = ClassUtil.createInstance(className);
			adapter.init(config);
		}catch(Exception exp){
			ExceptionUtil.wrapRuntimeException("The the adapter class initialization failed", exp);
		}

	}

	private IMessengerConnectionAdapter getAdapter(){
		return adapter;
	}
	
	@Override
	public boolean connect() {
		return adapter.connect();
	}

	@Override
	public boolean reConnect() {
		ExceptionUtil.createRuntimeException("Method is not implemented");
		return false;
	}

	@Override
	public void disconnecting() {
		getAdapter().disconnecting();
	}

	public boolean ping() {
		ExceptionUtil.createRuntimeException("Method is not implemented");
		return false;
	}

	public BuddyStatusEnum getBuddyStatus(String messengerID) {
		return getAdapter().getBuddyStatus(messengerID);
	}

	private GenericWindow getWindow(IMessengerWindowAdapter windowAdapter) {
		assert (windowAdapter != null);

		GenericWindow window = this.windowMap.get(windowAdapter.getId());
		if (window == null) {
			window = new GenericWindow(windowAdapter, this);
			this.windowMap.put(windowAdapter.getId(), window);
			this.fireWindowStarted(window);
		}
		return window;
	}

	@Override
	public boolean isConnected() {
		ExceptionUtil.createRuntimeException("Method is not implemented");
		return false;
	}

//	public void windowStarted(IMessengerWindowAdapter window) {
//		getWindow(window);
//		
//		log.info("EVENT: windowStarted ("
//				+ window.getMessengerUserUIDs()  + ")");
//	}
//	
//	public void windowEnded(IMessengerWindowAdapter window) {
//		this.fireWindowStopped(getWindow(window));
//		windowMap.remove(window.getId());
//		log.info("EVENT: conversationEnded ("
//				+ window.getMessengerUserUIDs()  + ")");
//		
//	}

	
	public void progressTyping(IMessengerWindowAdapter session, String userUID, String reason) {
		
		log.debug("EVENT: progressTyping" + session.getUserName(userUID) + " Reason: " + reason);
	}

	public void instantMessageReceived(IMessengerWindowAdapter window, String userUid,
			IChatletMessage message) {

		log.debug("EVENT: messageReceived" + message.toString());
		
		getWindow(window)
				.requestReceived(message, userUid);

	}


	public void startActiveWindow(String buddyUid, IStartActiveWindowCallback callback) throws StartActiveWindowException{
		getAdapter().startActiveWindow(buddyUid, callback);
	}

	public void userAdded(String userName, boolean fireEvent) {
		this.addMessengerUser(userName, fireEvent);
	}

	public void userRemoved(String userName, boolean fireEvent) {
		this.removeMessengerUser(userName, fireEvent);
	}

//	public void fireEvent(IMessengerWindowAdapter dialog, String eventType, Object... params) {
//		IWindow context = getWindow(dialog).getWindowContext();
//		if(context == null){
//			log.warn(String.format("Cannot find the conversation context for dialog(%s), and event(%s) cannot be fired.", dialog.getId(), eventType));
//			//todo add non-conversation event
//			return;
//		}
//		RaptorTaskQueue.getInstance(dialog.getId()).addTask(null);
////				new WindowEventTask(
////						eventType, 
////						context, 
////						params));
//		
//	}

	public void internalWindowStopped(IMessengerWindow window) {
		GenericWindow genericWindow = (GenericWindow)window;
		this.windowMap.remove(genericWindow.getId());
	}

	@Override
	public void inviteBuddy(String userId) throws InviteBuddyException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isInviteBuddySupported() {
		// TODO Auto-generated method stub
		return false;
	}






}
