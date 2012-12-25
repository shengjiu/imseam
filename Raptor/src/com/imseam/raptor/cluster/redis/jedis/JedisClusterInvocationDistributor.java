package com.imseam.raptor.cluster.redis.jedis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.util.SafeEncoder;

import com.imseam.chatlet.IConnection;
import com.imseam.chatlet.IContext;
import com.imseam.chatlet.IEventErrorCallback;
import com.imseam.chatlet.IIdentiable.UidType;
import com.imseam.chatlet.IWindow;
import com.imseam.chatlet.exception.IdentifierNotExistingException;
import com.imseam.common.util.StringUtil;
import com.imseam.raptor.IChatletApplication;
import com.imseam.raptor.chatlet.UidHelper;
import com.imseam.raptor.cluster.IClusterInvocation;
import com.imseam.raptor.cluster.IClusterInvocationDistributor;
import com.imseam.raptor.standard.LocalClusterRequestDistributor;
import com.imseam.serialize.SerializerUtil;

public class JedisClusterInvocationDistributor implements IClusterInvocationDistributor {
	
	private static Log log = LogFactory.getLog(JedisClusterInvocationDistributor.class);
	
	private LocalClusterRequestDistributor localDistributor = new LocalClusterRequestDistributor();
	
	public static final byte [] GLOBAL_EVENT_CHANNEL = SafeEncoder.encode("GLOBAL_EVENT_CHANNEL");
	
	public static final byte [] LOCAL_SERVER_CHANNEL = SafeEncoder.encode(UidHelper.getLocalServerId());
	

	@Override
	public void initApplication(IChatletApplication application) {
		localDistributor.initApplication(application);
		
		JedisInstance.initialize(application);
		
		
		Thread t = new Thread(new Runnable() {
			public void run() {
				Jedis jedis = JedisInstance.getStandaloneJedis();
				jedis.subscribe(new BinaryJedisPubSub() {
					@SuppressWarnings("unchecked")
					public void onMessage(byte[] channel, byte[] message) {
						if (Arrays.equals(LOCAL_SERVER_CHANNEL, channel)) {
							localServerMessageReceived(message);
							return;
						}
						if (Arrays.equals(GLOBAL_EVENT_CHANNEL, channel)) {
							globalEventReceived(message);
							return;
						}

						assert (false);
					}

					public void onSubscribe(byte[] channel, int subscribedChannels) {
						System.out.println("binarySubscribe onSubscribe Channel: " + new String(channel));
						System.out.println("binarySubscribe onSubscribe subscribedChannels: " + subscribedChannels);

					}

					public void onUnsubscribe(byte[] channel, int subscribedChannels) {
						System.out.println("binarySubscribe onUnsubscribe Channel: " + new String(channel));
						System.out.println("binarySubscribe onUnsubscribe subscribedChannels: " + subscribedChannels);
					}

					public void onPSubscribe(byte[] pattern, int subscribedChannels) {
					}

					public void onPUnsubscribe(byte[] pattern, int subscribedChannels) {
					}

					public void onPMessage(byte[] pattern, byte[] channel, byte[] message) {
					}
				}, GLOBAL_EVENT_CHANNEL, LOCAL_SERVER_CHANNEL);

			}
		}, "Jedis - subsriber");
		t.setDaemon(true);
		t.start();

	}
	
	private void globalEventReceived(byte[] message){
		JedisGlobalEvent event = (JedisGlobalEvent)SerializerUtil.deserialize(message);
		
		event.processEvent();
	}
	
	private void localServerMessageReceived(byte[] message){
		
		InvocationWrapper remoteInvocation = (InvocationWrapper)SerializerUtil.deserialize(message);
		IEventErrorCallback handler = remoteInvocation.getHandler();
		IClusterInvocation<? extends IContext> invocation = remoteInvocation.getInvocation();
		UidType type = remoteInvocation.getType();
		String [] targetUids = remoteInvocation.getTargetList().toArray(new String[remoteInvocation.getTargetList().size()]);
		
		if (type.equals(UidType.WINDOW)) {
			try {
				localDistributor.distributeWindowRequest(handler, (IClusterInvocation<IWindow>)invocation, targetUids);
			} catch (IdentifierNotExistingException e) {
				log.warn(e);
			}
			return;
		}
		if (type.equals(UidType.CONNECTION)) {
			try {
				localDistributor.distributeConnectionRequest(handler, (IClusterInvocation<IConnection>)invocation, targetUids);
			} catch (IdentifierNotExistingException e) {
				log.warn(e);
			}
			return;
		}

	}
	
	public static void publishGlobalEvent(JedisGlobalEvent event){
		
		Jedis jedis = null;
		try{
			jedis = JedisInstance.getJedisFromPool();
		
			byte[] bytes = SerializerUtil.serialize(event);
			
			jedis.publish(GLOBAL_EVENT_CHANNEL, bytes);
			
		}finally{
			JedisInstance.returnToPool(jedis);
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void distributeRequest(IEventErrorCallback handler, IClusterInvocation<? extends IContext> request, UidType idType, String targetUid) throws IdentifierNotExistingException {
		if (idType.equals(UidType.WINDOW)) {
			distributeWindowRequest(handler, (IClusterInvocation<IWindow>) request, targetUid);
			return;
		}
		if (idType.equals(UidType.CONNECTION)) {
			distributeConnectionRequest(handler, (IClusterInvocation<IConnection>) request, targetUid);
			return;
		}
		assert (false);
	}
	

	@SuppressWarnings("unchecked")
	public void distributeJedisRequest(UidType idType, IEventErrorCallback handler, IClusterInvocation<? extends IContext> request, Map<String, List<String>> serverTargetsMap) throws IdentifierNotExistingException {
		
		
//		List<String> localTargets = serverTargetsMap.remove(UidHelper.getLocalServerId());
//		if(localTargets != null && localTargets.size() > 0){
//			if (idType.equals(UidType.WINDOW)) {
//				this.localDistributor.distributeWindowRequest(handler, (IClusterInvocation<IWindow>) request, localTargets.toArray(new String[localTargets.size()]));
//			}
//			if (idType.equals(UidType.CONNECTION)) {
//				this.localDistributor.distributeConnectionRequest(handler, (IClusterInvocation<IConnection>) request, localTargets.toArray(new String[localTargets.size()]));
//			}
//		}
		
		if(serverTargetsMap.size() > 0){
			Jedis jedis = null;
			try{
				jedis = JedisInstance.getJedisFromPool();
				Pipeline pipeline = jedis.pipelined();
				pipeline.multi();
				for(String targetServerUid : serverTargetsMap.keySet()){
					List<String> remoteTargetList = serverTargetsMap.get(targetServerUid);
					InvocationWrapper invocation = new InvocationWrapper(handler, request, idType, remoteTargetList);
					byte[] bytes = SerializerUtil.serialize(invocation);
					pipeline.publish(SafeEncoder.encode(targetServerUid), bytes);
				}
				pipeline.exec();
				pipeline.sync();
			}finally{
				JedisInstance.returnToPool(jedis);
			}

		}
	}
	
	private Map<String, List<String>> serverTargetsMap(String... targetUids){
		Map <String, List<String>> serverTargetsMap = new HashMap<String, List<String>>();
		
		for(String targetUid :targetUids){
			String serverUid = UidHelper.parseLocalServerId(targetUid);
			List<String> targetList = serverTargetsMap.get(serverUid);
			if(targetList == null){
				targetList = new ArrayList<String>();
				serverTargetsMap.put(serverUid, targetList);
			}
			targetList.add(targetUid);
		}
		return serverTargetsMap;
	}

	@Override
	public void distributeConnectionRequest(IEventErrorCallback handler, IClusterInvocation<IConnection> request, String... connectionUids) throws IdentifierNotExistingException {
		Map <String, List<String>> serverTargetsMap = new HashMap<String, List<String>>();
		
		for(String connectionUid :connectionUids){
			
			String serverUid = ConnectionUidLocalCache.getServerUid(connectionUid);
			if(StringUtil.isNullOrEmpty(serverUid)){
				log.error("The local connectionuid cache doesn't inlcude the connection uid");
				continue;
			}
			List<String> targetList = serverTargetsMap.get(serverUid);
			if(targetList == null){
				targetList = new ArrayList<String>();
				serverTargetsMap.put(serverUid, targetList);
			}
			targetList.add(connectionUid);
		}
		
		
		distributeJedisRequest(UidType.CONNECTION, handler, request, serverTargetsMap);
	}
	
	@Override
	public void distributeWindowRequest(IEventErrorCallback handler, IClusterInvocation<IWindow> request, String... windowUids) throws IdentifierNotExistingException {
		Map<String, List<String>> serverTargetsMap = serverTargetsMap(windowUids);
		distributeJedisRequest(UidType.WINDOW, handler, request, serverTargetsMap);
	}
	
}
