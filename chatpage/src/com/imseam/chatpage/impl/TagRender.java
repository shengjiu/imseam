package com.imseam.chatpage.impl;

import com.imseam.chatlet.IAttributes;
import com.imseam.chatlet.IMessageSender;
import com.imseam.chatpage.ChatPageRenderException;
import com.imseam.chatpage.IResponseRender;
import com.imseam.chatpage.context.ChatpageContext;
import com.imseam.chatpage.tag.Tag;

public class TagRender implements IResponseRender {
	
//	private static Log log = LogFactory.getLog(TagRender.class);
	

	private Tag tag;
	
	public TagRender(Tag tag){
		assert(tag != null);
		this.tag = tag;
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
		tag.render(ChatpageContext.current());
	}
}
