package com.imseam.chatpage.menu;

import java.util.List;

import com.imseam.chatpage.context.ChatpageContext;

public class DynamicMenu {
	
	private String menuDataComponentName;
	
	private int recordNumberPerPage;
	
	private int currentPage = 1; 
	
	private boolean displayAll;
	
	DynamicMenu(String menuDataComponentName){
		this.menuDataComponentName = menuDataComponentName;
	}
	
	private MenuData getMenuData(){
//		return (MenuData) ChatPageManager.getInstance().getExpressionSolver().getValue(menuDataComponentName);
		return (MenuData) ChatpageContext.current().evaluateExpression(menuDataComponentName);
	}
	
	
	public int getTotalRecordCount(){
		return this.getMenuData().size();
	}
	
	public int getCurrentPageStartRecord(){
		return (currentPage - 1)* recordNumberPerPage;
	}
	
	public int getCurrentPage(){
		return currentPage;
	}
	
	public int getTotalPageCount(){
		if((this.getMenuData().size() % this.recordNumberPerPage) == 0){
			return this.getMenuData().size() / this.recordNumberPerPage;
		}
		return this.getMenuData().size() / this.recordNumberPerPage + 1;
	}
	
	public void setRecordNumberPerPage(int recordNumberPerPage){
		this.recordNumberPerPage = recordNumberPerPage;
	}
	
	public int getRecordNumberPerPage(){
		return this.recordNumberPerPage;
	}
	
	public boolean isDiaplayAll(){
		return displayAll;
	}
	
	public void setDisplayAll(boolean displayAll){
		this.displayAll = displayAll;
	}
	
	public DynamicMenuItem getMenuItem(int menuItemNumber){
		return this.getMenuData().get(menuItemNumber - 1);
	}
	
	public List<DynamicMenuItem> current(){
		if(this.displayAll){
			return this.getMenuData().getAll();
		}
		int fromIndex = (this.currentPage -1) * this.recordNumberPerPage;
		int toIndex = this.currentPage * this.recordNumberPerPage - 1;
		if(toIndex > this.getMenuData().size()){
			toIndex = this.getMenuData().size();
		}
		return this.getMenuData().subList(fromIndex, toIndex);
	}
	
	public List<DynamicMenuItem> previous(){
		if(this.currentPage > 1){
			this.currentPage = this.currentPage - 1;
		}
		return current();
			
	}	
	public List<DynamicMenuItem> next(){
		if(this.currentPage < (this.getTotalPageCount())){
			this.currentPage = this.currentPage + 1;
		}
		return current();
		
	}
	
	public List<DynamicMenuItem> first(){
		this.currentPage = 1;
		return current();
		
	}
	
	public List<DynamicMenuItem> last(){
		this.currentPage = this.getTotalPageCount();
		return current();
	}
	
	public List<DynamicMenuItem> all(){
		this.displayAll = true;
		return current();
	}
	
	
	public List<DynamicMenuItem> gotoPage(int gotoPage){
		if(gotoPage >= 1 && gotoPage <= this.getTotalPageCount()){
			this.currentPage = gotoPage;
		}
		return current();
	}
	
	public void sortBy(String sortBy){
		this.getMenuData().sortBy(sortBy);
	}

}
