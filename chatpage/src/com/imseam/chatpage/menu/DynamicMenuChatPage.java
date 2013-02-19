package com.imseam.chatpage.menu;

import java.util.List;
import java.util.Map;

import com.imseam.chatlet.IAttributes;
import com.imseam.chatlet.IMessageSender;
import com.imseam.chatlet.IUserRequest;
import com.imseam.chatpage.ChatPageRenderException;
import com.imseam.chatpage.IResponseRender;
import com.imseam.chatpage.context.ChatpageContext;
import com.imseam.chatpage.impl.AbstractChatPage;
import com.imseam.chatpage.impl.PageAction;
import com.imseam.common.util.ClassUtil;
import com.imseam.common.util.ExceptionUtil;

public class DynamicMenuChatPage extends AbstractChatPage {
	
	private final static String GOTO_PAGE_NUMBER_KEY = "gotoPageNumber";
	
	private final static String MENU_DATA_COMPONENT = "menu-data-component";
	
	private DynamicMenu menu;

	public DynamicMenuChatPage(DynamicMenu menu){
		this.menu = menu;
	}
	
	

	
	@Override
	public void init(String viewID, String parentPath, IResponseRender body,
			IResponseRender help, List<PageAction> actionList, Map<String, String> params) {
		super.init(viewID, parentPath, body, help, actionList, params);
		String menuDataComponent = this.getParam(MENU_DATA_COMPONENT);
		ExceptionUtil.assertStringNotNullOrEmptyAfterTrim(menuDataComponent,
									"You must define \"menu-data-component\" for the dynamic menu chat page: " + this.getFullPathViewID());
//		Object component = ChatPageManager.getInstance().getExpressionSolver().getValue(menuDataComponent);
		Object component = ChatpageContext.current().evaluateExpression(menuDataComponent);
		
		ExceptionUtil.assertNotNull(component, "Cannot create imseam component: " + menuDataComponent);
		
		Class<?> componentClass = component.getClass();
		if(!ClassUtil.isSubclass(componentClass, MenuData.class)){
			ExceptionUtil.createRuntimeException("The bean class(%s) of component(%s) is not a subclass of MenuData.class", menuDataComponent, componentClass);
		}
		
		menu = new DynamicMenu(menuDataComponent);
	}


	public void redenerBody(String input, IAttributes request, IMessageSender responseSender) throws ChatPageRenderException {
		getBody().render(input, request,responseSender);
		List<DynamicMenuItem> menuItemList = menu.current();
		for(DynamicMenuItem menuItem : menuItemList){
			menuItem.renderContent();
		}
		
	}

	public void redenerHelp(String input, IAttributes request, IMessageSender responseSender) throws ChatPageRenderException {
		getHelp().render(input, request, responseSender);
		
	}

	

	/*
	 * If return null, the chatflow will stay on the same chat page
	 * MenuPage parsers
	 * <parsers>
        	<string-parser pattern="p|b|back" outcome="previous"/>
        	<string-parser pattern="n|next" outcome="next"/>
        	<string-parser pattern="f|first" outcome="first"/>
        	<string-parser pattern="l|last" outcome="last"/>
        	<string-parser pattern="a|all" outcome="all"/>
        	<string-parser pattern="p|page" outcome="page"/>
        	<regexp-parser pattern="\d+" outcome="numberSelected"/>
        	<regexp-parser pattern="(g|goto|go)\s*\(d+)" outcome="gotoPage">
        	   <regexp-parseresult parameter-name="gotoPageNumber" group-number="2"/>
        	</regexp-parser>
        </parsers>
	 * 
	 * @see com.imseam.chatpage.ChatPage#parseInput(java.lang.String)
	 */
	@Override
	public String parseAndProcessInput(IUserRequest request) {
		
		String outcome = super.parseAndProcessInput(request);
		if(outcome == null) return null;
		
		CommandEnum commandEnum = CommandEnum.parseCommand(outcome);
		assert(commandEnum != null);
		return commandEnum.execute(menu, request.getInput());
	}
	
	
	
	public enum CommandEnum {

		NUMBERSELECTED {		
			public String execute(DynamicMenu menu, String input){
				int menuItemNumber = Integer.parseInt(input);
				DynamicMenuItem menuItem = menu.getMenuItem(menuItemNumber);
				if(menuItem != null){
					return menuItem.selectAndGetOutcome();
				}else{
					return null;
				}
			}		
		},
		PREVIOUS {		
			public String execute(DynamicMenu menu, String input){
				if(menu.getCurrentPage() != 1){
					displayList(menu.previous());
				}
				return null;
			}		
		},
		NEXT {
			public String execute(DynamicMenu menu, String input){
				if(menu.getCurrentPage() < (menu.getTotalPageCount())){
					displayList(menu.next());
				}
				return null;
			}		
		},
		FIRST{
			public String execute(DynamicMenu menu, String input){
				if(menu.getCurrentPage() != 1){
					displayList(menu.first());
				}
				return null;
			}		
		},
		LAST{
			public String execute(DynamicMenu menu, String input){
				if(menu.getCurrentPage() != (menu.getTotalPageCount())){
					displayList(menu.next());
				}
				return null;
			}
		},
		ALL{
			public String execute(DynamicMenu menu, String input){
				if(!menu.isDiaplayAll()){
					menu.setDisplayAll(true);
					displayList(menu.all());
				}
				return null;
			}
		},
		PAGE{
			public String execute(DynamicMenu menu, String input){
				if(menu.isDiaplayAll()){
					menu.setDisplayAll(false);
					displayList(menu.current());
				}
				return null;
			}
		},
		GOTOPAGE{
			public String execute(DynamicMenu menu, String input){
//				String gotoPageNumberStr = (String)ChatPageManager.getInstance().getExpressionSolver().getValue(GOTO_PAGE_NUMBER_KEY);
				String gotoPageNumberStr = (String)ChatpageContext.current().evaluateExpression(GOTO_PAGE_NUMBER_KEY);
				if(gotoPageNumberStr == null) return null;
				int gotoPage = Integer.parseInt(gotoPageNumberStr);
				if(gotoPage != menu.getCurrentPage() && gotoPage >= 1 && gotoPage <= menu.getTotalPageCount()){
					displayList(menu.gotoPage(gotoPage));
				}
				return null;
			}
		};

		public abstract String execute(DynamicMenu menu, String input);
		
		private static void displayList(List<DynamicMenuItem> menuItemList){
			for(DynamicMenuItem menuItem : menuItemList){
				menuItem.renderContent();
			}
		}
		
		private static CommandEnum parseCommand(String outcome){
			for(CommandEnum commandEnum: values()){
				if(commandEnum.toString().equalsIgnoreCase(outcome)){
					return commandEnum;
				}
			}
			return null;
		}

		
	}
}
