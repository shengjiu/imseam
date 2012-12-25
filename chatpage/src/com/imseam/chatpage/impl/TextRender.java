package com.imseam.chatpage.impl;

import java.util.HashMap;
import java.util.Locale;

import com.imseam.chatlet.IAttributes;
import com.imseam.chatlet.IMessageSender;
import com.imseam.chatlet.IUserRequest;
import com.imseam.chatpage.ChatPageRenderException;
import com.imseam.chatpage.IResponseRender;

public class TextRender implements IResponseRender {
	
//	private static Log log = LogFactory.getLog(TextRender.class);
	
	private static HashMap<String, String> SPECIAL_CHARACTER_MAP = new HashMap<String, String>();
	
	static{
		SPECIAL_CHARACTER_MAP.put("\\\\n", "\n");
		SPECIAL_CHARACTER_MAP.put("\\\\t", "   ");
	}
	
	private String content;
	
	public TextRender(String content){
		assert(content != null);
		this.content = content.trim();
		for(String specialKey : SPECIAL_CHARACTER_MAP.keySet()){
			this.content = this.content.replaceAll(specialKey, SPECIAL_CHARACTER_MAP.get(specialKey));
		}
	}
	
//	public void render(OutputStream out) throws ChatPageRenderException{
//		//The InterpolatorResponseFilter will do the filtering 
//		if(content != null){
//			try{
//				out.write(content.getBytes());
//			}catch(IOException ioExp){
//				log.warn("Exception happened during render text content", ioExp);
//			}
//		}
//	}
	
//	public String render() throws ChatPageRenderException {
//		return content;
//	}
	
	public void render(String input, IAttributes request, IMessageSender responseSender) throws ChatPageRenderException {
		assert(responseSender != null);
		Locale locale = null;
		if(request instanceof IUserRequest){
			locale = ((IUserRequest)request).getRequestFromChannel().getLocale();
		}
		
		responseSender.send(Interpolator.getInstance().interpolate(locale, content));
		
	}
}
