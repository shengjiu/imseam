package com.imseam.chatpage.impl.menu;

import com.imseam.chatpage.impl.ChatPageData;
import com.imseam.chatpage.impl.ItemIteratorUtil;

class MenuPageData extends ChatPageData{
	
	private MenuPaging menuPaging = null;
	private Object itemsObj = null;

	public MenuPageData(String fullPathViewId, String pageType, Object itemsObj, int recordNumberPerPage, boolean displayAll) {
		super(fullPathViewId, pageType);
		int totalCount = ItemIteratorUtil.getForEachItemsSize(itemsObj);
		this.menuPaging = new MenuPaging(totalCount, recordNumberPerPage, displayAll);
		this.itemsObj = itemsObj;
	}

	public Object getItemsObj() {
		return itemsObj;
	}

	public void setItemsObj(Object itemsObj) {
		this.itemsObj = itemsObj;
	}

	public MenuPaging getMenuPaging() {
		return menuPaging;
	}
	
}