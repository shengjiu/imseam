package com.imseam.chatpage.impl.menu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IAttributes;
import com.imseam.chatlet.IMessageSender;
import com.imseam.chatlet.IUserRequest;
import com.imseam.chatpage.ChatPageRenderException;
import com.imseam.chatpage.IParser;
import com.imseam.chatpage.IParser.ParseResult;
import com.imseam.chatpage.IResponseRender;
import com.imseam.chatpage.context.ChatpageContext;
import com.imseam.chatpage.impl.AbstractChatPage;
import com.imseam.chatpage.impl.ChatPageData;
import com.imseam.chatpage.impl.PageAction;
import com.imseam.chatpage.tag.ForEachTag;
import com.imseam.common.util.StringUtil;

public class DefaultMenuChatPage extends AbstractChatPage {

	private static Log log = LogFactory.getLog(DefaultMenuChatPage.class);
	
	static private Map<IParser, MenuCommandEnum> parserCommandMap = new HashMap<IParser, MenuCommandEnum>();
	
	static private void addMenuCommand(MenuCommandEnum command){
		parserCommandMap.put(command.getParser(), command);
	}
	
	static{
		addMenuCommand(MenuCommandEnum.BACK);
		
		addMenuCommand(MenuCommandEnum.NEXT);
		
		addMenuCommand(MenuCommandEnum.FIRST);
		
		addMenuCommand(MenuCommandEnum.LAST);
		
		addMenuCommand(MenuCommandEnum.ALL);
		
		addMenuCommand(MenuCommandEnum.PAGING);
		
		addMenuCommand(MenuCommandEnum.NUMBERSELECTED);
		
		addMenuCommand(MenuCommandEnum.GOTOPAGE);
		
	}
	
	
	private IResponseRender header = null;
	private IResponseRender footer = null;
	private MenuForEachTag menuTag = null;
	private int pageSize = 10;
	private String menuItemSelectedAction = null;
	private static String DISPLAY_ALL = "ALL";
	private boolean displayAllInitally = false;
	

	public DefaultMenuChatPage() {
	}

	
	public void init(String viewID, String parentPath, IResponseRender header, 
			IResponseRender menuItem, IResponseRender footer, IResponseRender help, 
			String var, String items, String pageSizeString, 
			String menuItemSelectedAction,  List<PageAction> actionList, Map<String, String> params) {
		
		
		this.header = header;
		this.footer = footer;
		menuTag = new MenuForEachTag(var, items, menuItem);
		
		if(!StringUtil.isNullOrEmptyAfterTrim(pageSizeString)){
			displayAllInitally = DISPLAY_ALL.equalsIgnoreCase(pageSizeString);
			if(!displayAllInitally){	
				pageSize = Integer.valueOf(pageSizeString.trim());
			}
		}
		
		this.menuItemSelectedAction = menuItemSelectedAction;

		init(viewID, parentPath, null, help, actionList, params);
		
	}
	
	private boolean isNew(){
		ChatPageData pageData = ChatpageContext.current().getCurrentPageData();
		
		if(pageData == null || ! this.getFullPathViewID().equals(pageData.getFullPathViewId())){
			return true;
		}
		return false;
	}
	
	
	public void redenerBody(String input, IAttributes request, IMessageSender responseSender) throws ChatPageRenderException {
		if(isNew()){
			Object itemsObj = menuTag.calculateItemsObject(ChatpageContext.current());
			
			MenuPageData menuPageData = new MenuPageData(this.getFullPathViewID(), 
					DefaultMenuChatPage.class.toString(), 
					itemsObj, pageSize, displayAllInitally);
			
			ChatpageContext.current().setCurrentPageData(menuPageData);
		}

		
		header.render(input, request, responseSender);
		menuTag.render(ChatpageContext.current());
		footer.render(input, request, responseSender);
		
		
	}

	public void redenerHelp(String input, IAttributes request, IMessageSender responseSender) throws ChatPageRenderException {
		if (getHelp() != null)
			getHelp().render(input, request, responseSender);
	}

	/*
	 * If return null, the chatflow will stay on the same chat page
	 * MenuPage parsers

	 * 
	 * @see com.imseam.chatpage.ChatPage#parseInput(java.lang.String)
	 */
	@Override
	public String parseAndProcessInput(String input, IUserRequest request) {
		
		String outcome = super.parseAndProcessInput(input, request);
		
		if(outcome != null) return outcome;
		
		MenuCommandEnum commandEnum = parseInput(input, request);
		
		if(commandEnum == null){
			//todo add warning
			return null;
		}
		
		String processResult = commandEnum.process(input, request, this);
		
		return processResult;
	}

	String getMenuItemSelectedAction() {
		return menuItemSelectedAction;
	}
	
	private MenuCommandEnum parseInput(String input, IUserRequest request){
		
		for(Map.Entry<IParser, MenuCommandEnum> parserCommandEntry : parserCommandMap.entrySet()){
			ParseResult parseResult = parserCommandEntry.getKey().parseInput(input, request);
			if(parseResult.isRecognized()){
				return parserCommandEntry.getValue();
			}
		}
		return null;
	}
	
	


	private class MenuForEachTag extends ForEachTag {
		
		private IResponseRender menuItem; 
		public MenuForEachTag(String var, String items, IResponseRender menuItem) {
			super(var, items, "true", null);
			assert (items != null);
			this.menuItem = menuItem;
		}
		
		private MenuPageData getPageData(){
			ChatPageData pageData = ChatpageContext.current().getCurrentPageData();
			if(pageData == null) return null;
			return (MenuPageData) pageData;
		}
		
		@Override
		public Object getForEachItemsObject(){
			MenuPageData menuPageData = getPageData();
			if(menuPageData == null) return null;
			return menuPageData.getItemsObj();
		}
		
		@Override
		protected int getBegin(ChatpageContext context) {
			if(DefaultMenuChatPage.this.isNew()){
				return 0;
			}else{
				return ((MenuPageData)ChatpageContext.current().getCurrentPageData()).getMenuPaging().getCurrentPageStartRecord();
			}
		}

		@Override
		protected int getPageSize(ChatpageContext context) {
			return ((MenuPageData)ChatpageContext.current().getCurrentPageData()).getMenuPaging().getRecordNumberPerPage();
		}
		
		@Override
		protected void renderChildren(ChatpageContext context){
			try {
				String input = null;
				if(context.getRequest() instanceof IUserRequest){
					input = ((IUserRequest) context.getRequest()).getInput(); 
				}
				menuItem.render(input, context.getRequest(), context.getMessengeSender());
			} catch (ChatPageRenderException e) {
				log.warn(e);
			}
		}

	}
	
	
	

}
