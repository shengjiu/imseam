package com.imseam.test.connector.netty.server;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.serialization.ClassResolvers;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;

import com.imseam.test.Constants;
import com.imseam.test.IEventListener;
import com.imseam.test.Message;
import com.imseam.test.message.AcceptInvitationMessage;
import com.imseam.test.message.BuddyAddedToWindowMessage;
import com.imseam.test.message.InvitationMessage;
import com.imseam.test.message.RpcRequestMessage;
import com.imseam.test.message.RpcResponseMessage;
import com.imseam.test.message.SignOutMessage;
import com.imseam.test.message.StatusChangeMessage;
import com.imseam.test.message.TextMessage;
import com.imseam.test.message.UserLoginMessage;
import com.imseam.test.message.WindowClosedMessage;
import com.imseam.test.message.WindowOpennedMessage;

public class Server {
	
    private static final Logger logger = Logger.getLogger(
    		Server.class.getName());
	
	private ExecutorService executorService = Executors.newCachedThreadPool(); 
	
	private ServerBootstrap bootstrap = null;
	
	private Channel serverChannel = null;
	
	private String hostAddress = null; //InetAddress.getLocalHost().getHostName();
	
	public String getHostAddress() {
		return hostAddress;
	}


	private IEventListener eventListener = null;
	
	private Map<String, TestUserData> userMap = new ConcurrentHashMap<String, TestUserData>();
	
	public Server(IEventListener eventListener, ServerRPCServiceFactory rpcServiceFactory){
		this.eventListener = eventListener;
		ServerRPCServiceFactory.setFactory(rpcServiceFactory);
	}
	
	public TestUserData getUser(String username){
		return userMap.get(username);
	}
	
	public void userOnline(String username, String initStatus, Channel channel){
		TestUserData user = new TestUserData(username, initStatus, channel);
		userMap.put(username, user);
	}
	
	public String getBuddyStatus(String username){
		TestUserData user =this.userMap.get(username); 
		if(user == null){
			return Constants.offline;
		}
		return user.getStatus();
	}
	
	public void startServer(final int port){
		try {
			hostAddress = InetAddress.getLocalHost().getHostName() +":" + port;
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		
		Thread t = new Thread(new Runnable() {
			public void run() {
		        bootstrap = new ServerBootstrap(
		                new NioServerSocketChannelFactory(
		                		executorService,
		                		executorService));

		        // Set up the pipeline factory.
		        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
		            public ChannelPipeline getPipeline() throws Exception {
//						return Channels.pipeline(new JsonDecoder(), new JsonEncoder(),
								return Channels.pipeline(new ObjectEncoder(), new ObjectDecoder(ClassResolvers.cacheDisabled(getClass().getClassLoader())), 
										new SimpleChannelUpstreamHandler(){
									@Override
								    public void handleUpstream(
								            ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
								        if (e instanceof ChannelStateEvent &&
								            ((ChannelStateEvent) e).getState() != ChannelState.INTEREST_OPS) {
								            logger.info(e.toString());
								        }
								        super.handleUpstream(ctx, e);
								    }

								    @Override
								    public void channelConnected(
								            ChannelHandlerContext ctx, ChannelStateEvent e) {
								        // Send the first message if this handler is a client-side handler.
								    }

								    @Override
								    public void messageReceived(
								            ChannelHandlerContext ctx, MessageEvent e) {
								    	
								    	
								    	Server.this.messageReceived(e);
								    }

								    @Override
								    public void exceptionCaught(
								            ChannelHandlerContext ctx, ExceptionEvent e) {
								        logger.log(
								                Level.WARNING,
								                "Unexpected exception from downstream.",
								                e.getCause());
								        //e.getChannel().close();
								    }						
								});
							}
						});
		            	
		            	
		        // Bind and start to accept incoming connections.
		        serverChannel = bootstrap.bind(new InetSocketAddress(port));
		        
				
			}
		}, "Test Netty Connector - Netty - " + hostAddress);
		t.setDaemon(true);
		t.start();	
	}
	
	public void stopServer(){
		ChannelFuture cf = serverChannel.close();
		cf.awaitUninterruptibly();
		executorService.shutdown();
		bootstrap.releaseExternalResources();
	}
	
	public void sendMessage(String username, Message message){
        ChannelFuture lastWriteFuture = null;
        
        Channel channel = userMap.get(username).getChannel();
        
        lastWriteFuture = channel.write(message);
        
	}
	
	private void messageReceived(MessageEvent e){
    	Message message = (Message) e.getMessage();
		message.setReceivedTime(new Date());
//    	String messageFor = message.targetId();
    	
    	if(message instanceof UserLoginMessage){
    		UserLoginMessage userLoginMessage = (UserLoginMessage)message;
    		this.userOnline(userLoginMessage.username(), userLoginMessage.getStatus(), e.getChannel());
    		eventListener.onUserLogin((UserLoginMessage)message);
    		return;
    	}
    	
    	if(message instanceof InvitationMessage){
    		eventListener.onInvitation((InvitationMessage)message);
    		return;
    	}
    	if(message instanceof WindowOpennedMessage){
    		eventListener.onWindowOpened((WindowOpennedMessage)message);
    		return;
    	}

    	if(message instanceof TextMessage){
    		eventListener.onTextMessage((TextMessage)message);
//    		System.out.println(((TextMessage)message).getContent());
    		return;
    	}

    	if(message instanceof WindowClosedMessage){
    		eventListener.onWindowClosed((WindowClosedMessage)message);
    		return;
    	}
    	
    	if(message instanceof BuddyAddedToWindowMessage){
    		eventListener.onBuddyAddedToWindow((BuddyAddedToWindowMessage)message);
    		return;
    	}
    	
    	if(message instanceof RpcRequestMessage){
    		RpcRequestMessage requestMessage = (RpcRequestMessage)message;
    		Object returnObject = requestMessage.getRemoteInvocation().invoke(null);
    		
    		RpcResponseMessage responseMessage = new RpcResponseMessage(requestMessage.getCallbackId(), returnObject, requestMessage.getFrom());
    		e.getChannel().write(responseMessage);
    		return;
    	}
    	
    	if(message instanceof AcceptInvitationMessage){
    		eventListener.onInvitationAccepted((AcceptInvitationMessage)message);
    		return;
    	}
    	
    	
    	if(message instanceof StatusChangeMessage){
    		eventListener.onBuddyStatusChange((StatusChangeMessage)message);
    		return;
    	}
    	
    	if(message instanceof SignOutMessage){
    		eventListener.onBuddySignOut((SignOutMessage)message);
    		return;    		
    	}
    	assert(false);

	}	

	
	public static void main(String [] args) throws InterruptedException{
		Server server = new Server(new IEventListener(){

			@Override
			public void onUserLogin(UserLoginMessage message) {
				logger.info("onUserLogin");
				
			}

			@Override
			public void onWindowOpened(WindowOpennedMessage message) {
				logger.info("onWindowOpened");
				
			}

			@Override
			public void onWindowClosed(WindowClosedMessage message) {
				logger.info("onWindowClosed");
				
			}

			@Override
			public void onBuddyAddedToWindow(BuddyAddedToWindowMessage message) {
				logger.info("onBuddyAddedToWindow");				
			}

			@Override
			public void onTextMessage(TextMessage message) {
				logger.info("onTextMessage");				
			}

			@Override
			public void onInvitation(InvitationMessage message) {
				logger.info("onInvitation");				
			}

			@Override
			public void onInvitationAccepted(AcceptInvitationMessage message) {
				logger.info("onInvitationAccepted");				
			}


			@Override
			public void onBuddyStatusChange(StatusChangeMessage message) {
				logger.info("onBuddyStatusChange");				
			}

			@Override
			public void onBuddySignOut(SignOutMessage message) {
				logger.info("onBuddySignOut");				
			}
			
		}, new ServerRPCServiceFactory(){

			@Override
			public ServerRPCService getService(String username) {
				
				return new ServerRPCService(){

					private static final long serialVersionUID = 1589173830423049040L;

					@Override
					public String startWindow(String... buddies) {
						String uuid = UUID.randomUUID().toString();
						logger.info("window id: " + uuid);
						return uuid;
					}

					@Override
					public void windowStarted(String windowId) {
						// TODO Auto-generated method stub
						
					}
				};
			}
			
		});
		server.startServer(18080);
		Thread.currentThread().join();
	}
	
}
