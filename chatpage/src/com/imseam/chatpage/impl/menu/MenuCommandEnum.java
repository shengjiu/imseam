package com.imseam.chatpage.impl.menu;

import java.util.ArrayList;
import java.util.List;

import javax.el.MethodExpression;

import com.imseam.chatlet.IMessageSender;
import com.imseam.chatlet.IUserRequest;
import com.imseam.chatpage.ChatPageRenderException;
import com.imseam.chatpage.IParser;
import com.imseam.chatpage.context.ChatpageContext;
import com.imseam.chatpage.impl.ItemIteratorUtil;
import com.imseam.chatpage.impl.RegexpParser;
import com.imseam.chatpage.impl.StringParser;
import com.imseam.chatpage.impl.RegexpParser.RegexpParseResultTemplate;

public enum MenuCommandEnum {
	
	NUMBERSELECTED {		
		public String process(String input, IUserRequest request, DefaultMenuChatPage menuChatPage){
			MenuPaging menuPaging = getMenuPaging();
			int menuItemNumber = Integer.parseInt(input);
			if(menuItemNumber >= 1 && menuItemNumber <= menuPaging.getTotalRecordCount()){
				ChatpageContext context = ChatpageContext.current();
				MethodExpression expression = context.getExpressionFactory().createMethodExpression(context.getELContext(), menuChatPage.getMenuItemSelectedAction(), String.class, new Class[]{Object.class});
				MenuPageData pageData = ((MenuPageData)ChatpageContext.current().getCurrentPageData());
				Object menuItem = ItemIteratorUtil.getForEachItem(pageData.getItemsObj(), menuItemNumber -1);
				Object result = expression.invoke(context.getELContext(), new Object[]{menuItem});
				
				if(result != null){
					return (String)result;
				}else{
					renderPageAgain(input, request, menuChatPage);
				}
					
			}
			return null;
		}		
		
		public IParser getParser(){
			return new RegexpParser("^\\d+", null);
		}
	},
	
	
	BACK {		
		public String process(String input, IUserRequest request, DefaultMenuChatPage menuChatPage){
			getMenuPaging().previous();
			renderPageAgain(input, request, menuChatPage);
			return null;
		}	
		
		public IParser getParser(){
			return new StringParser("b|back");
		}
	},

	NEXT {
		public String process(String input, IUserRequest request, DefaultMenuChatPage menuChatPage){
			getMenuPaging().next();
			renderPageAgain(input, request, menuChatPage);
			return null;
		}		
		
		public IParser getParser(){
			return new StringParser("n|next");
		}
	},
	FIRST{
		public String process(String input, IUserRequest request, DefaultMenuChatPage menuChatPage){
			getMenuPaging().first();
			renderPageAgain(input, request, menuChatPage);
			return null;
		}		
		
		public IParser getParser(){
			return new StringParser("f|first");
		}
	},
	LAST{
		public String process(String input, IUserRequest request, DefaultMenuChatPage menuChatPage){
			getMenuPaging().last();
			renderPageAgain(input, request, menuChatPage);
			return null;
		}
		
		public IParser getParser(){
			return new StringParser("l|last");
		}
	},
	ALL{
		public String process(String input, IUserRequest request, DefaultMenuChatPage menuChatPage){
			MenuPaging menuPaging = getMenuPaging();
			if(!menuPaging.isDiaplayAll()){
				menuPaging.setDisplayAll(true);
				renderPageAgain(input, request, menuChatPage);
			}else{
				//todo add warning
			}
			return null;
		}
		
		public IParser getParser(){
			return new StringParser("a|all");
		}
	},
	PAGING{
		public String process(String input, IUserRequest request, DefaultMenuChatPage menuChatPage){
			MenuPaging menuPaging = getMenuPaging();
			if(menuPaging.isDiaplayAll()){
				menuPaging.setDisplayAll(false);
				menuPaging.current();
				renderPageAgain(input, request, menuChatPage);
			}else{
				//todo add warning
			}
			return null;
		}
		public IParser getParser(){
			return new StringParser("p|paging");
		}
	},
	GOTOPAGE{
		public String process(String input, IUserRequest request, DefaultMenuChatPage menuChatPage){
			String gotoPageNumberStr = (String)request.getParameter(GOTO_PAGE_NUMBER_KEY);
			if(gotoPageNumberStr == null){
				//todo add warning
				return null;
			}
			int gotoPage = 0;
			try{
				gotoPage = Integer.parseInt(gotoPageNumberStr);
			}catch(Exception exp){
				//todo add warning
				return null;
			}
			MenuPaging menuPaging = getMenuPaging(); 
			
			if(gotoPage != menuPaging.getCurrentPage() && gotoPage >= 1 && gotoPage <= menuPaging.getTotalPageCount()){
				menuPaging.gotoPage(gotoPage);
				renderPageAgain(input, request, menuChatPage);
			}else{
				//todo add warning
			}
			return null;
		}
		
		public IParser getParser(){
			List<RegexpParseResultTemplate> resultList = new ArrayList<RegexpParseResultTemplate>();
			resultList.add(new RegexpParseResultTemplate(2, MenuCommandEnum.GOTO_PAGE_NUMBER_KEY));
			return new RegexpParser("^(g|goto|go)\\s*(\\d+)", resultList);
		}
	};

	public abstract String process(String input, IUserRequest request, DefaultMenuChatPage menuChatPage);
	
	public abstract IParser getParser();
	
	private static String GOTO_PAGE_NUMBER_KEY = DefaultMenuChatPage.class + "GOTO_PAGE_NUMBER_KEY";
	
	
	MenuPaging getMenuPaging(){
		return ((MenuPageData)ChatpageContext.current().getCurrentPageData()).getMenuPaging();
	}
	
	void renderPageAgain(String input, IUserRequest request, DefaultMenuChatPage menuChatPage){
		IMessageSender responseSender = ChatpageContext.current().getWindow().getMessageSender();
		try {
			menuChatPage.redenerBody(input, request, responseSender);
		} catch (ChatPageRenderException e) {
			e.printStackTrace();
		}
	}

}
