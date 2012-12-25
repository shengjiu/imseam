package com.imseam.chatpage.config.test;

import com.imseam.chatpage.config.ChatPagesInfo;
import com.imseam.chatpage.config.util.ChatPageConfigReader;



public class UnMarshallerTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try{
		
		ChatPagesInfo pages = ChatPageConfigReader.parserChatPagesConfigFile("C:\\workspace\\Imseam\\chatpage\\src\\com\\imseam\\chatpage\\config\\test\\chat-pages-sample.csp");
		System.out.println(ChatPageConfigReader.marshallChatPagesConfig(pages));

//		ChatPagesInfo pagetest = ChatPageConfigReader.parserChatPagesConfigFile("C:\\workspace\\Imseam\\chatpage\\src\\com\\imseam\\chatpage\\config\\test\\NewFile.xml");
//		System.out.println(ChatPageConfigReader.marshallChatPagesConfig(pagetest));

		}catch(Exception exp){
			exp.printStackTrace();
		}

	}

}
