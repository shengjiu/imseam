package com.imseam.chatpage.menu;

import java.util.List;

public interface MenuData {
	
	int size();
	
	/*
	 * the from and to will be included
	 */
	List<DynamicMenuItem> subList(int fromIndex, int toIndex);
	
	List<DynamicMenuItem> getAll();
	
	DynamicMenuItem get(int number);
	
	void sortBy(String sortBy);

}
