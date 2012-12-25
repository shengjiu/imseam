package com.imseam.freamworktest;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import com.imseam.cdi.chatlet.components.CDIMeeting;
import com.imseam.cdi.context.IMWindowScoped;
import com.imseam.chatpage.context.ChatpageContext;


@IMWindowScoped @Named("echoBean")
public class EchoBean{
	
	private @Inject Instance<CDIMeeting> meeting;
	
	private List<String> foreachItems = new ArrayList<String>();
	
	private List<String> menuItems = new ArrayList<String>();
	
	public EchoBean(){
		for(int i = 0; i < 11; i ++){
			foreachItems.add("Foreach "+ i);
		}
		for(int i = 0; i < 56; i ++){
			menuItems.add("menu "+ i);
		}

	}
	
	public List<String> getForeachItems() {
		return foreachItems;
	}

	public List<String> getMenuItems() {
		return menuItems;
	}

	public String echo(){
		String userInput = ChatpageContext.current().evaluateStringExp("#{request.input}");
		meeting.get().send(userInput);
		return "echo";
	}
	
	public String menuSelected(Object item){
		System.out.println("Menu item selected: " + item);
		return null;
	}

}
