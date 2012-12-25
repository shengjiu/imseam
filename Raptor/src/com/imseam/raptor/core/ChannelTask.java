package com.imseam.raptor.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.raptor.chatlet.ChannelContext;
import com.imseam.raptor.chatlet.WindowContext;
import com.imseam.raptor.threading.PrioritizedTask;



//Each conversation will contain it's own meetingRequestHandler;
public class ChannelTask implements PrioritizedTask<Object, Long> {

	private static final Log log = LogFactory.getLog(ChannelTask.class);

	private WindowContext windowContext = null;
	
	private ChannelContext [] channels =null;
	
	private boolean isAdd = true;
	
	private boolean isTimeout = false;

	private long time = System.currentTimeMillis();

	private ChannelTask(boolean isAdd, WindowContext windowContext, ChannelContext ... channels) {
		assert (windowContext != null);
		assert ((channels != null) && (channels.length > 0));
		
		this.isAdd = isAdd;

		this.windowContext = windowContext;
		this.channels = channels;
	}
	
	private ChannelTask(boolean isAdd, boolean isTimeout, WindowContext conversationContext, ChannelContext ... channels) {
		this(isAdd, conversationContext, channels);
		assert (conversationContext != null);
		assert ((channels != null) && (channels.length > 0));
		
		this.isAdd = isAdd;
		this.isTimeout = isTimeout;

		this.windowContext = conversationContext;
		this.channels = channels;
	}

	public static ChannelTask createAddChannelTask(WindowContext conversationContext, ChannelContext ... channels){
		return new ChannelTask(true, conversationContext, channels);
	}
	
	public static ChannelTask createRemoveChannelTask(WindowContext conversationContext, ChannelContext ... channels){
		return new ChannelTask(false, conversationContext, channels);
	}
	
	public static ChannelTask createChannelTimeoutTask(WindowContext conversationContext, ChannelContext ... channels){
		return new ChannelTask(false, true, conversationContext, channels);
	}

	public Long getPriority() {

		return Long.valueOf(time); 
	}

	// will be invoked in the conversation thread
	public Object call() throws Exception {
		try{
			for(ChannelContext channel : channels){
				if(isAdd){
					if(windowContext.getChannelByBuddyUid(channel.getUid()) != null){
						log.warn(String.format("The channel for user (%s)is existing", channel.getUid()));
						continue;
					}
					windowContext.addOnboardChannel(channel);
//					Lifecycle.beginChannel(channel);
				}else{
					if(windowContext.getChannelByBuddyUid(channel.getBuddy().getUid()) == null){
						log.warn(String.format("The channel for user (%s)is NOT existing", channel.getUid()));
						continue;
					}
					
					if(this.isTimeout && !channel.timeoutConfirm()){
						log.warn(String.format("The channel for user (%s)is renewed", channel.getUid()));
						continue;
					}
						
//					Lifecycle.endChannel(channel);
					windowContext.removeOnboardChannel(channel.getBuddy().getUid());
					channel.getUserSession().removeChannel(channel);
					
					if(windowContext.getOnboardChannels().size() == 0){
//						Lifecycle.endConversation(windowContext);
						windowContext.closeWindow();
					}
				}
			}
		}catch(Exception exp){
			log.error(String.format("Exception happened during processing %s Channel task", isAdd ? "add" : "remove"), exp);
			throw exp;
		}finally{
			
		}
		return null;
	}
}
