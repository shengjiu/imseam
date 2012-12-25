package com.imseam.chatpage.impl;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IUserRequest;
import com.imseam.chatpage.IChatPage;
import com.imseam.chatpage.IParser;
import com.imseam.chatpage.IParser.ParseResult;
import com.imseam.chatpage.IResponseRender;
import com.imseam.common.util.StringUtil;

public abstract class AbstractChatPage implements IChatPage{
	
	
	private static Log log = LogFactory.getLog(AbstractChatPage.class);
	
	private String viewID;
	private String parentPath;
	private String fullPathViewID;
	private Map<String, String> params;
	private List<PageAction> actionList;
	
	private IResponseRender body;
	private IResponseRender help;

	

	public void init(String viewID, String parentPath, IResponseRender body,
			IResponseRender help, List<PageAction> actionList, Map<String, String> params) {
		assert(!StringUtil.isNullOrEmptyAfterTrim(viewID));
		assert(!StringUtil.isNullOrEmptyAfterTrim(parentPath));
		
		if(viewID.indexOf(PathSeperator) >= 0){
			log.error("View ID cannot contain " + PathSeperator + ":" + viewID);
		}
		this.viewID = viewID;
		
		if(!parentPath.startsWith(PathSeperator)){
			log.error("Parent path must start with root: " + PathSeperator + ":" + parentPath);
		}
		this.parentPath = parentPath;
		if(!parentPath.endsWith(PathSeperator)){
			parentPath = parentPath + PathSeperator;
		}
		fullPathViewID = parentPath + viewID;
		this.params = params;
		
		this.actionList = actionList;
		
		this.body = body;
		this.help = help;
	}
	
	protected String getParam(String key){
		return params.get(key);
	}
	
	protected Set<String> getKeySet(){
		return params.keySet();
	}
	
	public String getViewID() {
		return this.viewID;
	}
	
	public String getParentPath() {
		return this.parentPath;
	}

	public String getFullPathViewID() {
		return this.fullPathViewID;
	}
	
	public String parseAndProcessInput(String input, IUserRequest request) {
		if(this.actionList == null){
			return null;
		}
		for(PageAction action : actionList){
			if(action.getParserList() == null) continue;
			for(IParser parser: action.getParserList()){
				ParseResult result = parser.parseInput(input, request); 
				if(result != null && result.isRecognized()){
					if(result.getParameterMap() != null){
						for(Entry<String, String> entry : result.getParameterMap().entrySet()){
							request.setAttribute(entry.getKey(), entry.getValue());
						}
					}
					return action.getActionExcecutor() != null ? action.getActionExcecutor().execute(input, request) : null;
				}
			}
		}
		return null; 
	}

	protected IResponseRender getBody() {
		return body;
	}

	protected IResponseRender getHelp() {
		return help;
	}


}
