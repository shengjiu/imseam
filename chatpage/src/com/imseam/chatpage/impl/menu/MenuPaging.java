package com.imseam.chatpage.impl.menu;


public class MenuPaging {
	
	
	private int recordNumberPerPage;
	
	private int currentPage = 1; 
	
	private int totalCount = 0;
	
	private boolean displayAll = false;
	
	public MenuPaging(int totalCount, int recordNumberPerPage, boolean displayAll){
		this.totalCount = totalCount;
		this.recordNumberPerPage = recordNumberPerPage;
		this.displayAll = displayAll;
	}
	
	
	public int getTotalRecordCount(){
		return totalCount;
	}
	
	public int getCurrentPageStartRecord(){
		return (currentPage - 1)* recordNumberPerPage;
	}
	
	public int getCurrentPage(){
		return currentPage;
	}
	
	public boolean isDiaplayAll(){
		return displayAll;
	}
	
	public void setDisplayAll(boolean displayAll){
		this.displayAll = displayAll;
	}
	
	public int getTotalPageCount(){
		if(this.displayAll) return 1;
		
		if((this.getTotalRecordCount() % this.recordNumberPerPage) == 0){
			return this.getTotalRecordCount() / this.recordNumberPerPage;
		}
		return this.getTotalRecordCount() / this.recordNumberPerPage + 1;
	}
	
	public void setRecordNumberPerPage(int recordNumberPerPage){
		this.recordNumberPerPage = recordNumberPerPage;
	}
	
	public int getRecordNumberPerPage(){
		if(this.displayAll) return totalCount;
		return this.recordNumberPerPage;
	}
	
	
	public int current(){
		
		if(this.displayAll) return 0;
		
		return (this.currentPage -1) * this.recordNumberPerPage;
	}
	
	public int previous(){
		if(this.currentPage > 1){
			this.currentPage = this.currentPage - 1;
		}
		return current();
	}	
	public int next(){
		if(this.currentPage < (this.getTotalPageCount())){
			this.currentPage = this.currentPage + 1;
		}
		return current();
	}
	
	public int first(){
		this.currentPage = 1;
		return current();
		
	}
	
	public int last(){
		this.currentPage = this.getTotalPageCount();
		return current();
	}
	

	
	public int gotoPage(int gotoPage){
		if(gotoPage >= 1 && gotoPage <= this.getTotalPageCount()){
			this.currentPage = gotoPage;
		}
		return current();
	}
	

}
