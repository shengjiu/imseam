package com.imseam.raptor.chatlet;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.imseam.chatlet.IChannel;
import com.imseam.chatlet.IUserRequest;
import com.imseam.common.util.StringUtil;

public abstract class AbstractChatletRequest extends AbstractContext implements IUserRequest {

//	private static Log log = LogFactory.getLog(AbstractChatletRequest.class);

	

	private Date requestTimeStamp;
	
	private String requestUID;
	
	private IChannel channelContext = null;
	
	private Locale locale = null;
	
	private Map<String, String> parameterMap = new HashMap<String,String>();
	
	protected AbstractChatletRequest(IChannel channelContext,
			Locale locale){
		this(channelContext, locale, new Date());
	}
	
	protected AbstractChatletRequest(IChannel channelContext,
			Locale locale,
			Date requestTimeStamp){
		super(false);
		this.channelContext = channelContext;
		this.requestTimeStamp = requestTimeStamp;
		this.locale = locale;
		requestUID = UUID.randomUUID().toString();
		
		assert(channelContext != null);
		assert(requestTimeStamp != null);
		assert(!StringUtil.isNullOrEmptyAfterTrim(requestUID));
	}

	public Date getRequestTimeStamp() {
		return requestTimeStamp;
	}

	public String getRequestUid() {
		return this.requestUID;
	}

	public IChannel getSenderMessengerChannel() {
		return channelContext;
	}
	
	public Locale getRequestLocale(){
		return locale;
	}
	
	public Set<String> getParameterNames() {
		return parameterMap.keySet();
	}

	public String getParameter(String name) {
		return parameterMap.get(name);
	}
	
	public void setParameter(String key, String value){
		parameterMap.put(key, value);
	}
	
	public String removeParameter(String key){
		return parameterMap.remove(key);
	}
	
	public IChannel getRequestFromChannel() {
		return this.channelContext;
	}
	
	public String getInput(){
		return this.getRequestContent().getMessageContent().toString();
	}
	
	@Override
	public String toString() {
		return "AbstractChatletRequest [requestTimeStamp=" + requestTimeStamp + ", requestUID=" + requestUID + ", channelContext=" + channelContext + ", locale=" + locale + ", parameterMap="
				+ parameterMap + ", toString()=" + super.toString() + "]";
	}
	
}
