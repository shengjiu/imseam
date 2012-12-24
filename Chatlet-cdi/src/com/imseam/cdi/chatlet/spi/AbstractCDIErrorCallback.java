package com.imseam.cdi.chatlet.spi;

import com.imseam.cdi.chatlet.event.ConnectionErrorCallbackRequest;
import com.imseam.cdi.chatlet.event.WindowErrorCallbackRequest;
import com.imseam.cdi.weld.WeldEngineHelper;
import com.imseam.chatlet.IConnection;
import com.imseam.chatlet.IContext;
import com.imseam.chatlet.IEventErrorCallback;
import com.imseam.chatlet.IIdentiable.UidType;
import com.imseam.chatlet.IWindow;
import com.imseam.common.util.ExceptionUtil;

public abstract class AbstractCDIErrorCallback implements IEventErrorCallback {


	private static final long serialVersionUID = 4571437200681219402L;
	
	private String eventSenderUid;
	private UidType senderIdType;
	
	public AbstractCDIErrorCallback(String eventSenderUid, UidType senderIdType){
		this.eventSenderUid = eventSenderUid;
		this.senderIdType = senderIdType;
	}
	

	@Override
	public String getEventSenderUid() {
		return this.eventSenderUid;
	}

	@Override
	public UidType getSenderIdType() {
		return this.senderIdType;
	}

	@Override
	final public void handleException(IContext senderContext, String sourceUid, Exception exp) {
		if(senderIdType.equals(UidType.WINDOW)){
			IConnection connection = (IConnection)senderContext;
			ConnectionErrorCallbackRequest request = new ConnectionErrorCallbackRequest(connection, sourceUid, exp);
			try{
				WeldEngineHelper.getInstance().getLifecycle().beginRequest(request);
				handleCDIEventError(senderContext, sourceUid, exp);
			}finally{
				WeldEngineHelper.getInstance().getLifecycle().endRequest(request);
			}
			return;
		}
		if(senderIdType.equals(UidType.CONNECTION)){
			IWindow window = (IWindow) senderContext;
			WindowErrorCallbackRequest request = new WindowErrorCallbackRequest(window, sourceUid, exp);
			
			try{
				WeldEngineHelper.getInstance().getLifecycle().beginRequest(request);
				handleCDIEventError(senderContext, sourceUid, exp);
			}finally{
				WeldEngineHelper.getInstance().getLifecycle().endRequest(request);
			}
			return;
		}
		ExceptionUtil.createRuntimeException("The sender id type (%s) is not supported, the senderContext is %s", this.senderIdType, senderContext.getClass());
	}
	
	abstract protected void handleCDIEventError(IContext senderContext, String sourceUid, Exception exp); 

}
