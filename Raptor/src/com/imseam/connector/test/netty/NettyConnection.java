package com.imseam.connector.test.netty;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.BuddyStatusEnum;
import com.imseam.chatlet.IStartActiveWindowCallback;
import com.imseam.chatlet.config.ConnectionConfig;
import com.imseam.chatlet.exception.InviteBuddyException;
import com.imseam.chatlet.exception.StartActiveWindowException;
import com.imseam.common.util.ExceptionUtil;
import com.imseam.common.util.StringUtil;
import com.imseam.raptor.IChatletApplication;
import com.imseam.raptor.IMessengerWindow;
import com.imseam.raptor.chatlet.MessengerTextMessage;
import com.imseam.raptor.chatlet.UidHelper;
import com.imseam.raptor.core.AbstractConnection;
import com.imseam.test.Constants;
import com.imseam.test.IEventListener;
import com.imseam.test.Message;
import com.imseam.test.connector.netty.server.Server;
import com.imseam.test.connector.netty.server.ServerRPCService;
import com.imseam.test.connector.netty.server.ServerRPCServiceFactory;
import com.imseam.test.message.AcceptInvitationMessage;
import com.imseam.test.message.BuddyAddedToWindowMessage;
import com.imseam.test.message.InvitationMessage;
import com.imseam.test.message.SignOutMessage;
import com.imseam.test.message.StatusChangeMessage;
import com.imseam.test.message.TextMessage;
import com.imseam.test.message.UserLoginMessage;
import com.imseam.test.message.WindowClosedMessage;
import com.imseam.test.message.WindowOpennedMessage;


public class NettyConnection extends AbstractConnection  {
	private static Log log = LogFactory.getLog(NettyConnection.class);
	private Server server = null;
	private IEventListener eventListener = new NettyEventListener();
	private ServerRPCServiceFactory rpcServiceFactory = new NettyServerRPCServiceFactory();
	private int port = -1;
	private Map<String, String> statusMap = new ConcurrentHashMap<String, String>();
	private Map<String, NettyMessengerWindow> windowMap = new ConcurrentHashMap<String, NettyMessengerWindow>();
	

	protected NettyConnection(IChatletApplication application, ConnectionConfig config) {
		super(application, config);
	}

	@Override
	public void initialize() {
		if (StringUtil.isNullOrEmptyAfterTrim(config.get("port"))) {
			ExceptionUtil
					.createRuntimeException("\'port\' is not defined. The Netty Test Connection requires \'port\' to be defined in the init-params.");
		}
		
		port = Integer.parseInt(config.get("port"));
	}

	@Override
	public boolean connect() {
		server = new Server(eventListener, rpcServiceFactory);
		server.startServer(port);
		connectionStarted();
		return true;
	}

	@Override
	public boolean reConnect() {
		ExceptionUtil.createRuntimeException("Method is not implemented");
		return false;
	}

	@Override
	public boolean ping() {
		ExceptionUtil.createRuntimeException("Method is not implemented");
		return false;
	}

	@Override
	public void internalWindowStopped(IMessengerWindow window) {
		this.windowMap.remove(window.getWindowContext().getUid());	
	}

	@Override
	public boolean isConnected() {
		ExceptionUtil.createRuntimeException("Method is not implemented");
		return false;
	}

	@Override
	public BuddyStatusEnum getBuddyStatus(String messengerID) {
		String status = statusMap.get(messengerID);
		if(status == null){
			return BuddyStatusEnum.unknown;
		}
		if(Constants.online.equals(status)){
			return BuddyStatusEnum.online_available;
		}
		if(Constants.busy.equals(status)){
			return BuddyStatusEnum.online_busy;
		}

		if(Constants.offline.equals(status)){
			return BuddyStatusEnum.offline;
		}
		if(Constants.notbuddy.equals(status)){
			return BuddyStatusEnum.notexisting;
		}

		return BuddyStatusEnum.notexisting;
	}

	@Override
	public void inviteBuddy(String userId) throws InviteBuddyException {
		String status = this.statusMap.get(userId);
		if(!Constants.notbuddy.equals(status)){
			ExceptionUtil.createRuntimeException("Cannot invite an notbuddy userid: " + userId +", current status: " + status);
		}
		Message message = new InvitationMessage(server.getHostAddress(), userId);
		server.sendMessage(userId, message);
		
	}

	@Override
	public boolean isInviteBuddySupported() {
		return true;
	}

	@Override
	public void startActiveWindow(String buddyUid, IStartActiveWindowCallback callback) throws StartActiveWindowException {
		try{
			String userId = UidHelper.parseUseridFromBuddyUid(buddyUid);
			if(Constants.online.equals(statusMap.get(userId))){
				NettyMessengerWindow window = new NettyMessengerWindow(server, this, userId);
				log.debug("Netty messenger window id: " + window.getWindowContext().getUid());
				Message message = new WindowOpennedMessage(server.getHostAddress(), window.getWindowContext().getUid(), userId);
				server.sendMessage(userId, message);
				
				windowMap.put(window.getWindowContext().getUid(), window);
				fireWindowStarted(window);
				callback.windowStarted(window.getDefaultChannel());

			}else{
				callback.startWindowFailed(new StartActiveWindowException(String.format("Active conversation can only be started when the messenger user's (%s) current status is online_available!", buddyUid), buddyUid));
			}
		}catch(Exception ioExp){
			callback.startWindowFailed(new StartActiveWindowException(String.format("Call active conversation for user (%s) IO error", buddyUid), ioExp, buddyUid));
		}
	}

	@Override
	protected void disconnecting() {
		server.stopServer();
		windowMap.clear();
		statusMap.clear();
	}
	
	private NettyMessengerWindow getWindow(String windowId){
		return this.windowMap.get(windowId);
	}
	
	
	private class NettyEventListener implements IEventListener{

		@Override
		public void onUserLogin(UserLoginMessage message) {
			log.debug("onUserLogin");
			statusMap.put(message.getFrom(), message.getStatus());
			
			addMessengerUser(message.getFrom(), !Constants.notbuddy.equals(message.getStatus()));
			fireBuddyStatusChange(message.getFrom());
		}

		@Override
		public void onWindowOpened(WindowOpennedMessage message) {
			log.debug("onWindowOpened");
			ExceptionUtil.createRuntimeException("onWindowOpened should not be fired in the server");
		}

		@Override
		public void onWindowClosed(WindowClosedMessage message) {
			log.debug("onWindowClosed");
			String windowId = message.getTargetId();
			fireWindowStopped(getWindow(windowId));
			windowMap.remove(windowId);
		}

		@Override
		public void onBuddyAddedToWindow(BuddyAddedToWindowMessage message) {
			log.debug("onBuddyAddedToWindow");
			String windowId = message.getTargetId();
			getWindow(windowId).userJoin(message.getBuddyIds());

		}

		@Override
		public void onTextMessage(TextMessage message) {
			log.debug("onTextMessage");	
//			if(message.getContent().contains("startMeeting")){
//				System.out.println("client received message user: " + message.getFrom() + ", window owner:" + message.getTargetId() + message.getContent());
//			}
			String windowId = message.getTargetId();
			MessengerTextMessage messageWrapper = new MessengerTextMessage(message.getContent());
			NettyMessengerWindow window = getWindow(windowId);
			
//			if(message.getContent().contains("startMeeting") && window == null){
//				System.out.println("client received, window is null, message user: " + message.getFrom() + ", window owner:" + message.getTargetId() + message.getContent());
//			}
			
			window.requestReceived(messageWrapper, message.getFrom());
			
		}

		@Override
		public void onInvitation(InvitationMessage message) {
			log.debug("onInvitation");	
			addMessengerUser(message.getFrom(), true);
			AcceptInvitationMessage returnMessage = new AcceptInvitationMessage(message.getTargetId(), message.getFrom());
			server.sendMessage(message.getFrom(), returnMessage);
			
		}

		@Override
		public void onInvitationAccepted(AcceptInvitationMessage message) {
			log.debug("onInvitationAccepted");
			fireBuddyAdded(message.getFrom());
		}

		@Override
		public void onBuddyStatusChange(StatusChangeMessage message) {
			log.debug("onBuddyStatusChange");	
			fireBuddyStatusChange(message.getFrom());
		}

		@Override
		public void onBuddySignOut(SignOutMessage message) {
			log.debug("onBuddySignOut");		
			fireBuddySignOff(message.getFrom());
		}
		
	}
	
	private class  NettyServerRPCServiceFactory extends ServerRPCServiceFactory{

		@Override
		public ServerRPCService getService(final String username) {
			
			return new ServerRPCService(){

				private static final long serialVersionUID = 7256565060488783901L;

				@Override
				public String startWindow(String... buddies) {
					NettyMessengerWindow window = new NettyMessengerWindow(NettyConnection.this.server, NettyConnection.this, username);
					log.debug("Netty messenger window id: " + window.getWindowContext().getUid());
					Object oldWindow = windowMap.put(window.getWindowContext().getUid(), window);
					return window.getWindowContext().getUid();
				}

				@Override
				public void windowStarted(String windowId) {
					NettyMessengerWindow window = windowMap.get(windowId);
					fireWindowStarted(window);
				}
			};
		}
		
	}
	

}
