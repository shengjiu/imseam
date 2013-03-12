package com.imseam.raptor.standard;

import com.imseam.chatlet.IChatletMessage;
import com.imseam.chatlet.IMessageSender;
import com.imseam.raptor.IMessengerWindow;
import com.imseam.raptor.chatlet.MessengerTextMessage;

public class MessageSender implements IMessageSender {
	
	private IMessengerWindow messengerWindow = null;
	
	private StringBuffer textBuffer = new StringBuffer();
	
	public MessageSender(IMessengerWindow messengerWindow){
		this.messengerWindow = messengerWindow;
	}

	@Override
	public void send(IChatletMessage... responseMessages) {
		if(responseMessages == null ) return;
		
		for(IChatletMessage message : responseMessages){
			if(message instanceof MessengerTextMessage){
				textBuffer.append(message.getMessageContent());
//				System.out.println("send message:" + message.getMessageContent());
			}else{
				messengerWindow.sendResponse(message);
			}
		}
	}

	@Override
	public void send(String message) {
		if(message != null){
			
			textBuffer.append(message.replace("::n", "\n"));
//			System.out.println("send server message:" +message);
//			textBuffer.append(message);
////			textBuffer.append("\n");
		}
	}
	
	public String getTextMessage(){
		return textBuffer.toString();
	}

	@Override
	public void flush() {
		if(textBuffer.length() <=0 ) return;
		textBuffer.append("\n");
//		System.out.println("flush server message:" +textBuffer.toString() + ":::"+ messengerWindow.getDefaultChannel().getBuddy().getUserId());
		messengerWindow.sendResponse(new MessengerTextMessage(textBuffer.toString()));
		textBuffer.delete(0,textBuffer.length());
	}

}
