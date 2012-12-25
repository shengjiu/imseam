package com.imseam.chatpage.filter;

import java.util.List;

import com.imseam.chatlet.IMessageSender;
import com.imseam.chatlet.IUserRequest;
import com.imseam.chatpage.IChatPage;
import com.imseam.chatpage.IParser;
import com.imseam.chatpage.impl.ParserCarrier;

public class HelpCommandFilter extends ParserCarrier implements CommandFilter {
	
	public HelpCommandFilter(List<IParser> parserList){
		super(parserList);
	}
	
	public void doCommandFilter(IChatPage chatpage, IUserRequest request, String input,
			CommandFilterChain chain, IMessageSender responseSender) {
		try{
			chatpage.redenerHelp(input, request, responseSender);
		}catch(Exception exp){
			// only continue processing when renderHelp throws exception
			chain.doCommandFilter(chatpage, request, input, responseSender);			
		}
	}

}
