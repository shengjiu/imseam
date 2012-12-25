package com.imseam.webchat;




public interface ChatServerFacade{
	public void conversationStarted(String userUID, String UserName, String dialogID);

	public void conversationEnded(String dialogID) ;

	public void progressTyping(String dialogID, String reason) ;

	public void instantMessageReceived(String dialogID,
			String message);
	public void fireEvent(String dialogID, String eventType, Object... params);

}
