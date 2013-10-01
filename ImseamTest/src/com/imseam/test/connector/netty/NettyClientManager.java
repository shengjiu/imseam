package com.imseam.test.connector.netty;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.bootstrap.ClientBootstrap;
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
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.serialization.ClassResolvers;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;

import com.imseam.common.util.ExceptionUtil;
import com.imseam.test.IEventListener;
import com.imseam.test.Message;
import com.imseam.test.RemoteInvocation;
import com.imseam.test.message.AcceptInvitationMessage;
import com.imseam.test.message.BuddyAddedToWindowMessage;
import com.imseam.test.message.InvitationMessage;
import com.imseam.test.message.RpcMessage;
import com.imseam.test.message.RpcRequestMessage;
import com.imseam.test.message.RpcResponseMessage;
import com.imseam.test.message.TextMessage;
import com.imseam.test.message.UserLoginMessage;
import com.imseam.test.message.WindowClosedMessage;
import com.imseam.test.message.WindowOpennedMessage;

public class NettyClientManager {
	
    private static final Logger logger = Logger.getLogger(
    		NettyClientManager.class.getName());

	
	private static NettyClientManager instance = new NettyClientManager();
	
	private Map<String, Channel> serverChannelMap = new ConcurrentHashMap<String, Channel>();
	
	private Map<String, Channel> userChannelMap = new ConcurrentHashMap<String, Channel>();
	
	private ExecutorService executorService = Executors.newCachedThreadPool(); 
	
	private Map<String, IEventListener> eventListenerMap = new ConcurrentHashMap<String, IEventListener>();
	
	private final AtomicInteger seqNum = new AtomicInteger(0);
	
	private final Map<Integer, BlockingRpc> blockingMap = new ConcurrentHashMap<Integer, BlockingRpc>();
	
	private final int callbacktimeout = 10000;
	

	private NettyClientManager(){
		
	}
	
	public static NettyClientManager instance(){
		return instance;
	}
	
	public int getNextSeqId() {
		return seqNum.getAndIncrement();
	}
	
	private synchronized void registerCallback(int seqId, BlockingRpc blocking) {
		if (blockingMap.containsKey(seqId)) {
			throw new IllegalArgumentException("blocking already registered");
		}
		blockingMap.put(seqId, blocking);
	}
	
	public synchronized void connect(String host, int port, String username, String status, IEventListener listener) {
		eventListenerMap.put(username, listener);

		String key = host.trim() + port;
		
		Channel channel = serverChannelMap.get(key);
		
		if (channel == null) {

			ClientBootstrap bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(executorService, executorService));

			// Set up the pipeline factory.
			bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
				public ChannelPipeline getPipeline() throws Exception {
//					return Channels.pipeline(new JsonDecoder(), new JsonEncoder(),
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
					        
					    	Message message = (Message) e.getMessage();
					    	NettyClientManager.this.messageReceived(message);
					    }

					    @Override
					    public void exceptionCaught(
					            ChannelHandlerContext ctx, ExceptionEvent e) {
					        logger.log(
					                Level.WARNING,
					                "Unexpected exception from downstream.",
					                e.getCause());
					        e.getChannel().close();
					    }						
					});
				}
			});

			// Start the connection attempt.

			ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));

			// Wait until the connection attempt succeeds or fails.
			channel = future.awaitUninterruptibly().getChannel();
			if (!future.isSuccess()) {
				future.getCause().printStackTrace();
				bootstrap.releaseExternalResources();
				ExceptionUtil.createRuntimeException("Cannot connected to server: " + host + ":" + port);
			}
			serverChannelMap.put(key, channel);
		}
		this.userChannelMap.put(username, channel);
		sendMessage(username, new UserLoginMessage(username, status));
		

	}
		
	
	public void sendMessage(String username, Message message){
        ChannelFuture lastWriteFuture = null;
        
        Channel channel = this.userChannelMap.get(username);
        
        lastWriteFuture = channel.write(message);
        
	}
	
	public Object remoteCall(String username, RemoteInvocation remoteInvocation, RemoteInvocation callbackInvocation){
		
		int nextSeqId = this.seqNum.getAndIncrement();
		RpcMessage rpcMessage = new RpcRequestMessage(nextSeqId, remoteInvocation, username);
		
		BlockingRpc blocking = new BlockingRpc();
		registerCallback(nextSeqId, blocking);

		Channel channel = this.userChannelMap.get(username);
        
        channel.write(rpcMessage);

		synchronized(blocking) {
			while(!blocking.isDone()) {
				try {
					long before = System.currentTimeMillis();
					blocking.wait(callbacktimeout);
					long after = System.currentTimeMillis();
					if((after - before) >= callbacktimeout){
//						System.out.println("----------------- wait time out ----------------------------");
					}
				} catch (InterruptedException e) {
//					System.out.println("----------------- InterruptedException ----------------------------");
					ExceptionUtil.wrapRuntimeException(e);
				}
			}
		}
		Object returnedObject = blocking.response().getReturnedObject();
		if(callbackInvocation != null){
			return callbackInvocation.invoke(returnedObject);
		}
		return returnedObject;
		
		
	}
	
	public void addWindowEventListener(String windowId, IEventListener listener){
		this.eventListenerMap.put(windowId, listener);
	}

	public void removeWindowEventListener(String windowId){
		this.eventListenerMap.remove(windowId);
	}

	
	private void messageReceived(Message message){
		message.setReceivedTime(new Date());
    	String messageFor = message.getTargetId();
    	IEventListener eventListener = this.eventListenerMap.get(messageFor);
    	
    	if(eventListener == null){
        	if(message instanceof WindowOpennedMessage){
        		messageFor = ((WindowOpennedMessage) message).getBuddyIds()[0];
            	eventListener = this.eventListenerMap.get(messageFor);
        	}
    	}
        	
    	if(eventListener == null){
//    		System.out.println("--------------------------------------------------------------");
//    		if(message instanceof TextMessage){
//        		System.out.println("MessageReceived but eventListener is null, window: " + messageFor +", message: " + ((TextMessage)message).getContent());
//        	}else{
//        		System.out.println("MessageReceived but eventListener is null, window: " + messageFor +", message: " + message);
//        	}

    		return;
    	}
    	
    	
//    	System.out.println("message received, message for " +  messageFor);
    	
    	if(message instanceof InvitationMessage){
    		eventListener.onInvitation((InvitationMessage)message);
    		return;
    	}
    	
    	if(message instanceof AcceptInvitationMessage){
    		eventListener.onInvitationAccepted((AcceptInvitationMessage)message);
    		return;
    	}

    	if(message instanceof WindowOpennedMessage){
    		eventListener.onWindowOpened((WindowOpennedMessage)message);
    		return;
    	}

    	if(message instanceof WindowClosedMessage){
    		eventListener.onWindowClosed((WindowClosedMessage)message);
    		return;
    	}

    	if(message instanceof TextMessage){

    		eventListener.onTextMessage((TextMessage)message);
    		return;
    	}

    	if(message instanceof BuddyAddedToWindowMessage){
    		eventListener.onBuddyAddedToWindow((BuddyAddedToWindowMessage)message);
    		return;
    	}
    	
    	if(message instanceof RpcResponseMessage){
    		RpcResponseMessage responseMessage = (RpcResponseMessage)message;
    		int seqId = responseMessage.getCallbackId();
    		BlockingRpc callback = blockingMap.remove(seqId);
    		if(callback != null){
    			callback.done(responseMessage);
    		}
    		return;
    	}
    	assert(false);

	}
	
	private static class BlockingRpc {

		private volatile boolean done = false;
		private RpcResponseMessage response;
		
		public void done(RpcResponseMessage response) {
			this.response = response;
			if(response == null){
				logger.warning("response is null");
			}
			
			synchronized(this) {
				done = true;
				notify();
			}
		}
		
		public boolean isDone() {
			return done;
		}
		
		public RpcResponseMessage response(){
			return response;
		}
		
	}
	
	

}
