package com.imseam.raptor.standard;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IChannel;
import com.imseam.chatlet.IChatlet;
import com.imseam.chatlet.IChatletFilter;
import com.imseam.chatlet.IChatletMessage;
import com.imseam.chatlet.IMessageSender;
import com.imseam.chatlet.IUserRequest;
import com.imseam.common.util.StringUtil;
import com.imseam.raptor.IChatletApplication;
import com.imseam.raptor.IMessengerWindow;
import com.imseam.raptor.IRequestProcessor;
import com.imseam.raptor.chatlet.FilterChain;
import com.imseam.raptor.chatlet.MessengerTextMessage;
import com.imseam.raptor.chatlet.MessengerTextRequest;
import com.imseam.raptor.threading.PrioritizedTask;
import com.imseam.raptor.threading.RaptorTaskQueue;

public class RequestProcessor implements IRequestProcessor {
	private static Log log = LogFactory.getLog(RequestProcessor.class);

	private ChatletExceptionHandler exceptionHandler = new ChatletExceptionHandler();
	
	private IChatletApplication application;
	
	
	public RequestProcessor(){
		
	}

	public void initialize(Object source, Map<String, String> params) {
	}

	
	public void requestReceived(IChatletMessage message, String buddyUid, IMessengerWindow window){
		
		assert(message != null);
		assert(!StringUtil.isNullOrEmptyAfterTrim(buddyUid));
		assert(window != null);
		
		
		IChannel  channelContext = window.getWindowContext().getChannelByBuddyUid(buddyUid);
		assert(channelContext != null);
		
		IUserRequest chatletRequest = createChatletRequest(
				message, 
				channelContext,
				window,
				null);
		IMessageSender messageSender = window.getMessageSender();
		
		if(chatletRequest == null){
			log.warn(String.format("The request message is not supported. please look at the createChatletRequest method:"  + message));
			return;
		}
		
		if(message.getMessageContent().toString().contains("startMeeting")){
			System.out.println("before adding to RaptorTaskQueue: " + message.getMessageContent().toString());
		}
		
		RaptorTaskQueue.getInstance(window.getWindowContext().getUid()).addTask(new RequestTask(chatletRequest, messageSender));

	}

//	private IChatletRequest createRequestFromMessage(IChatletMessage message, String userName){
//		IMessengerChannelContext channelContext = getChannelContext(userName);
//		if(channelContext == null){
//			log.error(String.format("Cannot get the channelcontext for user(%s)", userName));
//		}
//		switch(message.getMessageType()){
//		case TextMessage: 
//			assert(message.getClass().equals(MessengerTextMessage.class));
//			return new MessengerTextRequest(message, channelContext, this, null);
//		}
//		return null;
//	}
	
//	private Class<? extends IChatletRequest> getMessage2RequestMap(MessageTypeEnum messageType){
//		switch(messageType){
//		case TextMessage: return GenericRequest.class;
//		}
//		return null;
//	}
	
	private IUserRequest createChatletRequest(IChatletMessage message, IChannel channelContext, IMessengerWindow window, Locale locale){
		
		if(message instanceof MessengerTextMessage){
			return new MessengerTextRequest((MessengerTextMessage)message, channelContext, locale);
		}
		
		return null;
	}

	
	class RequestTask implements PrioritizedTask <Object, Long>{
		
		private IUserRequest chatletRequest = null;
		
		private IMessageSender messageSender = null;
		
		RequestTask(IUserRequest chatletRequest, IMessageSender messageSender){
			assert(chatletRequest != null);
			assert(messageSender != null);
			this.chatletRequest = chatletRequest;
			this.messageSender = messageSender;
		}

		public Long getPriority() {
			
			return Long.valueOf(chatletRequest.getRequestTimeStamp().getTime());
		}

		public Object call() throws Exception {
			try {
				List<IChatletFilter> filterList = RequestProcessor.this.application.getFilterList();
				IChatlet chatlet = RequestProcessor.this.application.getChatlet();
				
				FilterChain filterChain = new FilterChain(filterList, chatlet);
				
				filterChain.doFilter(chatletRequest, messageSender);
//				String response = ((MessageSender)messageSender).getTextMessage();
//				String request = ((MessengerTextMessage)chatletRequest.getRequestContent()).getMessageContent(); 
//				if((!request.equalsIgnoreCase(response)) || StringUtil.isNullOrEmptyAfterTrim(response)){
//					System.out.println("request: " + request +", response: " + response);
//				}
				
				messageSender.flush();
				
			} catch (Throwable exp) {
				exceptionHandler.handleException(chatletRequest, messageSender, exp);
			}
			return null;
		}
	}


	@Override
	public void initApplication(IChatletApplication application) {
		assert(application != null);
		this.application = application;
		
	}
}
