package com.imseam.chatpage.config.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatpage.ChatPageManager;
import com.imseam.chatpage.IActionExecutor;
import com.imseam.chatpage.IParser;
import com.imseam.chatpage.IResponseRender;
import com.imseam.chatpage.config.ActionInfo;
import com.imseam.chatpage.config.ActionsInfo;
import com.imseam.chatpage.config.ChatPageInfo;
import com.imseam.chatpage.config.ChatPagesInfo;
import com.imseam.chatpage.config.ChatPagesInfo.GlobalParsersInfo;
import com.imseam.chatpage.config.CommandFilterInfo;
import com.imseam.chatpage.config.CommandFilterMappingInfo;
import com.imseam.chatpage.config.ForeachInfo;
import com.imseam.chatpage.config.GroupedParserInfo;
import com.imseam.chatpage.config.JavaParserInfo;
import com.imseam.chatpage.config.MenuPageInfo;
import com.imseam.chatpage.config.ParamInfo;
import com.imseam.chatpage.config.ParserInfo;
import com.imseam.chatpage.config.ParserRefInfo;
import com.imseam.chatpage.config.ParsersInfo;
import com.imseam.chatpage.config.RegexpParserInfo;
import com.imseam.chatpage.config.RegexpParserInfo.RegexpParseresultInfo;
import com.imseam.chatpage.config.ResponseInfo;
import com.imseam.chatpage.config.ResponseInfo.IncludeInfo;
import com.imseam.chatpage.config.StringParserInfo;
import com.imseam.chatpage.config.TextInfo;
import com.imseam.chatpage.filter.CommandFilter;
import com.imseam.chatpage.filter.CommandFilterManager;
import com.imseam.chatpage.filter.CommandFilterMapping;
import com.imseam.chatpage.filter.ComponentBasedCommandFilter;
import com.imseam.chatpage.impl.AbstractChatPage;
import com.imseam.chatpage.impl.DefaultChatPage;
import com.imseam.chatpage.impl.DefaultResponseRender;
import com.imseam.chatpage.impl.GroupedParser;
import com.imseam.chatpage.impl.IncludedRender;
import com.imseam.chatpage.impl.PageAction;
import com.imseam.chatpage.impl.RegexpParser;
import com.imseam.chatpage.impl.RegexpParser.RegexpParseResultTemplate;
import com.imseam.chatpage.impl.StringParser;
import com.imseam.chatpage.impl.TagRender;
import com.imseam.chatpage.impl.menu.DefaultMenuChatPage;
import com.imseam.chatpage.tag.ForEachTag;
import com.imseam.chatpage.tag.Tag;
import com.imseam.chatpage.tag.TextTag;
import com.imseam.common.util.ClassUtil;
import com.imseam.common.util.ExceptionUtil;
import com.imseam.common.util.StringUtil;

public class ConfigBasedObjectFactory {
	
	private static Log log = LogFactory.getLog(ConfigBasedObjectFactory.class);
	
	public static void loadChatPages(ChatPagesInfo chatPagesInfo, String parentPath){
		
		Map<String, IParser> gParserMap = new HashMap<String, IParser>();
		ArrayList<ChatPageInfo> chatPageInfoList = new ArrayList<ChatPageInfo>();
		ArrayList<MenuPageInfo> menuPageInfoList = new ArrayList<MenuPageInfo>();
		ArrayList<CommandFilterMapping> commandFilterMappingList = new ArrayList<CommandFilterMapping>();
		 
		
		for(Object pageOrParsersOrFilter : chatPagesInfo.getChatPageOrMenuPageOrGlobalParsers()){
			if(pageOrParsersOrFilter instanceof ChatPageInfo){
				chatPageInfoList.add((ChatPageInfo)pageOrParsersOrFilter);
				continue;
			}
			if(pageOrParsersOrFilter instanceof MenuPageInfo){
				menuPageInfoList.add((MenuPageInfo)pageOrParsersOrFilter);
				continue;
			}

			if(pageOrParsersOrFilter instanceof GlobalParsersInfo){
				List<IParser> parserList = createParserList((GlobalParsersInfo)pageOrParsersOrFilter, null);
				for(IParser parser : parserList){
					if(gParserMap.get(parser.getId()) != null){
						log.warn("The parser id is duplicated in the global parsers:" + parser.getId());
					}
					gParserMap.put(parser.getId(), parser);
				}
				continue;
			}
			
			if(pageOrParsersOrFilter instanceof CommandFilterInfo){
				CommandFilterInfo commandFilterInfo = (CommandFilterInfo)pageOrParsersOrFilter;
				CommandFilter commandFilter = ConfigBasedObjectFactory.createCommandFilter(commandFilterInfo);
				CommandFilterManager.getInstance().addFilter(commandFilterInfo.getFilterId(), commandFilter);
				continue;
			}

			if(pageOrParsersOrFilter instanceof CommandFilterMappingInfo){
				CommandFilterMappingInfo commandFilterMappingInfo = (CommandFilterMappingInfo)pageOrParsersOrFilter;
				commandFilterMappingList.add(new CommandFilterMapping(commandFilterMappingInfo.getFilterId(), commandFilterMappingInfo.getPathPattern()));
				continue;
			}
			
			ExceptionUtil.createRuntimeException("Object type is not expected!" + pageOrParsersOrFilter.getClass());
		}
		
		for(ChatPageInfo chatPageInfo : chatPageInfoList){
			
			ActionsInfo inPageActionsInfo = chatPageInfo.getActions();
			List<PageAction> actionList = null;
			if(inPageActionsInfo != null){
				actionList = createPageActionList(inPageActionsInfo, gParserMap);
			}
			IResponseRender body = ConfigBasedObjectFactory.createResponseContent(parentPath, chatPageInfo.getBody());
			IResponseRender help = ConfigBasedObjectFactory.createResponseContent(parentPath, chatPageInfo.getHelp());
			
			
			
			Map<String, String> params = null;
			if(chatPageInfo.getParams() != null){
				params = convertParams(chatPageInfo.getParams().getParam());
			}else{
				params = new HashMap<String, String>();
			}
			
			AbstractChatPage pageInstance = null;
			if(chatPageInfo.getComponent() != null){
				log.info("The component is used for view:" + chatPageInfo.getViewId());
//				pageInstance = new ComponentBasedChatPage(chatPageInfo.getComponent());
				if(chatPageInfo.getClassType()!= null){
					log.warn("The component is used and the classtype is ignored for chatpage :" + chatPageInfo.getViewId());
				}
				
			}else if(chatPageInfo.getClassType() != null){
				
				log.info("The classType is used for view:" + chatPageInfo.getViewId());

				pageInstance = (AbstractChatPage)ClassUtil.createInstance(chatPageInfo.getClassType());
				if(pageInstance == null){
					ExceptionUtil.createRuntimeException("Cannot create the class (%s) as chatpage for viewId(%s)", chatPageInfo.getClassType(), chatPageInfo.getViewId());
				}
			} else {
				log.info("The DefaultChatPage is used for view:" + chatPageInfo.getViewId());
				pageInstance = new DefaultChatPage();
			}
			pageInstance.init(chatPageInfo.getViewId(),	parentPath, body, help, actionList, params);
			ChatPageManager.getInstance().addChatPage(pageInstance);
		}
		
		for(MenuPageInfo menuPageInfo : menuPageInfoList){
			
			ActionsInfo inPageActionsInfo = menuPageInfo.getActions();
			List<PageAction> actionList = null;
			if(inPageActionsInfo != null){
				actionList = createPageActionList(inPageActionsInfo, gParserMap);
			}
			IResponseRender header = ConfigBasedObjectFactory.createResponseContent(parentPath, menuPageInfo.getHeader());
			IResponseRender menuItem = ConfigBasedObjectFactory.createResponseContent(parentPath, menuPageInfo.getMenuItem());
			IResponseRender footer = ConfigBasedObjectFactory.createResponseContent(parentPath, menuPageInfo.getFooter());
			IResponseRender help = ConfigBasedObjectFactory.createResponseContent(parentPath, menuPageInfo.getHelp());
			
			
			
			Map<String, String> params = null;
			if(menuPageInfo.getParams() != null){
				params = convertParams(menuPageInfo.getParams().getParam());
			}else{
				params = new HashMap<String, String>();
			}
			
			DefaultMenuChatPage pageInstance = new DefaultMenuChatPage();
			pageInstance.init(menuPageInfo.getViewId(), parentPath, 
					header, menuItem, footer, help, 
					menuPageInfo.getVar(), menuPageInfo.getItems(), menuPageInfo.getPageSize(),
					menuPageInfo.getMenuSelectedAction(), actionList, params);
			ChatPageManager.getInstance().addChatPage(pageInstance);
		}		
		
		//Add command filter mapping to the CommandFilterMapping after all commandfilter has been added.
		for(CommandFilterMapping commandFilterMapping : commandFilterMappingList){
			CommandFilterManager.getInstance().addFilterMapping(commandFilterMapping.getFilterId(), commandFilterMapping.getPathPattern());
		}

	}
	
	public static IParser createParser(ParserInfo parserInfo){
		IParser parser = null;
		if(parserInfo instanceof RegexpParserInfo){
			RegexpParserInfo rpInfo = (RegexpParserInfo)parserInfo;
			List<RegexpParseresultInfo> resultInfoList = rpInfo.getRegexpParseresult();
			List<RegexpParseResultTemplate> resultList = new ArrayList<RegexpParseResultTemplate>();
			parser = new RegexpParser(rpInfo.getPattern(), rpInfo.getId(), rpInfo.isNegative(), rpInfo.getFireConditionExpression(), resultList);
			for(RegexpParseresultInfo resultInfo: resultInfoList){
				RegexpParseResultTemplate result = new RegexpParseResultTemplate(resultInfo.getGroupNumber(), resultInfo.getParameterName());
				resultList.add(result);
			}
			return parser;
		}
		
		if(parserInfo instanceof StringParserInfo){
			StringParserInfo rpInfo = (StringParserInfo)parserInfo;
			String pattern = rpInfo.getPattern();
			String id = rpInfo.getId();
			Boolean negative = rpInfo.isNegative();
			String expression = rpInfo.getFireConditionExpression();
			
			parser = new StringParser(pattern, id, negative == null? false: negative, expression);
			
			
			return parser;
		}
		if(parserInfo instanceof JavaParserInfo){
			JavaParserInfo javaParserInfo = (JavaParserInfo)parserInfo;
			assert(StringUtil.isNullOrEmptyAfterTrim(javaParserInfo.getHandler()));
			try{
				Map<String, String> params = null;
				if(javaParserInfo.getParams() != null){
					params = convertParams(javaParserInfo.getParams().getParam());
				}else{
					params = new HashMap<String, String>();
				}				
				parser = (IParser)ClassUtil.createInstance(javaParserInfo.getHandler(), new Class[]{Map.class, String.class, String.class, IActionExecutor.class}, 
						params, javaParserInfo.getId());
			}catch(Exception exp){
				log.error(String.format("The java parser handler class must have a contructor having Map as parameters: %s(Map params, String id, String outcome, IAction action)", javaParserInfo.getHandler()), exp);
				ExceptionUtil.wrapRuntimeException(exp);
			}
			return parser;
		}
		return null;
	}
	
	private static List<PageAction> createPageActionList(ActionsInfo pageActionsInfo,Map<String, IParser> referenceMap){
		List<PageAction> pageActionList = new ArrayList<PageAction>();
		for(ActionInfo actionInfo : pageActionsInfo.getAction()){
			List<IParser> parserList = createParserList(actionInfo.getParsers(), referenceMap);
			
			Map<String, String> paramMap = null;
			if(actionInfo.getParams() != null){
				paramMap = convertParams(actionInfo.getParams().getParam());
			}else{
				paramMap = new HashMap<String, String>();
			}
			
			if(StringUtil.isNullOrEmptyAfterTrim(actionInfo.getOutcome()) && StringUtil.isNullOrEmptyAfterTrim(actionInfo.getClazz())){
				ExceptionUtil.createRuntimeException("For pageaction (id:%s), one of outcome (%s) or the action class (%s) must be set", actionInfo.getId(), actionInfo.getOutcome(), actionInfo.getClazz());
			}
			if((!StringUtil.isNullOrEmptyAfterTrim(actionInfo.getOutcome())) && (!StringUtil.isNullOrEmptyAfterTrim(actionInfo.getClazz()))){
				ExceptionUtil.createRuntimeException("For pageaction (id:%s), only one of outcome (%s) or the action class (%s) can be set", actionInfo.getId(), actionInfo.getOutcome(), actionInfo.getClazz());
			}
			PageAction pageAction = null;
			
			if(!StringUtil.isNullOrEmptyAfterTrim(actionInfo.getOutcome())){
				pageAction = new PageAction(actionInfo.getId(), parserList, actionInfo.getOutcome());
			}else{
				pageAction = new PageAction(actionInfo.getId(), parserList, actionInfo.getClazz(), actionInfo.getMethod(), paramMap);
			}
			
			pageActionList.add(pageAction);
		}
		return pageActionList;
	}
	
//	private static IActionExecutor createAction(ActionInfo actionInfo, Map<String, IParser> referenceMap){
//
//		IActionExecutor action = null;
//		if(actionInfo != null){
//			action = new ActionExecutorWrapper(actionInfo.getClazz(), actionInfo.getMethod(), convertParams(actionInfo.getParams()));
//		}
//		return action;
//	}
	
	public static List<IParser> createParserList(GlobalParsersInfo parsersInfo, Map<String, IParser> referenceMap){
		List<IParser> parserList = new ArrayList<IParser>();
		for(Object parserInfoObject: parsersInfo.getRegexpParserOrJavaParserOrStringParser()){
			IParser parser = null;
			if(parserInfoObject instanceof GroupedParserInfo){
				GroupedParserInfo parserGroupInfo = (GroupedParserInfo)parserInfoObject;
				parser = createParser(parserGroupInfo);
				
			}else{
				parser = createParser((ParserInfo)parserInfoObject);
			}
			if(parser != null){
				parserList.add(parser);
			}
		}
		return parserList;
	}
	
	
	public static IParser createParser(GroupedParserInfo groupParserInfo){
		List<IParser> parserList = new ArrayList<IParser>();
		for(Object inGroupParserInfoObject : groupParserInfo.getRegexpParserOrJavaParserOrStringParser()){
			if(inGroupParserInfoObject instanceof ParserInfo){
				IParser parser = createParser((ParserInfo)inGroupParserInfoObject);
				if(parser != null){
					parserList.add(parser);
				}
			}else{
				ExceptionUtil.createRuntimeException("Parser type is not expected: " + inGroupParserInfoObject.getClass() +", GroupedParser can only include regular parsers inherited from ParserInfo");
			}
		}
		return new GroupedParser(groupParserInfo.getName(), parserList);
		
	}
	
	public static List<IParser> createParserList(ParsersInfo parsersInfo, Map<String, IParser> referenceMap){
		List<IParser> parserList = new ArrayList<IParser>();
		for(Object parserInfoObject: parsersInfo.getRegexpParserOrJavaParserOrStringParser()){
			IParser parser = createParser(parserInfoObject, referenceMap);
			if(parser != null){
				parserList.add(parser);
			}
		}
		return parserList;
	}
	
	public static IParser createParser(Object parserInfoObject, Map<String, IParser> referenceMap){
		if(parserInfoObject instanceof ParserInfo){
			IParser parser = createParser((ParserInfo)parserInfoObject);
			if(parser != null){
				return parser;
			}else{
				log.warn("Parser cannot be created: " + parserInfoObject);
			}
			return null;
		}
		if(parserInfoObject instanceof ParserRefInfo){
			if(referenceMap == null){
				log.warn("The reference parser map is null");
				return null;
			}
			IParser parser = referenceMap.get(((ParserRefInfo)parserInfoObject).getRefId());
			if(parser != null){
				return parser;
			}else{
				log.warn("Parser cannot be found in the reference parser map: " + parserInfoObject);
			}
			return null;
		}
		
		ExceptionUtil.createRuntimeException("parser type is not expected!");
		return null;
		
	}

	public static CommandFilter createCommandFilter(CommandFilterInfo commandFilterInfo){
		List<IParser> parserList = createParserList(commandFilterInfo.getParsers(), null);
		
		Map<String, String> params = null;
		if(commandFilterInfo.getParams() != null){
			params = convertParams(commandFilterInfo.getParams().getParam());
		}else{
			params = new HashMap<String, String>();
		}
		
		if(commandFilterInfo.getComponent() != null){
			ComponentBasedCommandFilter commandFilter = new ComponentBasedCommandFilter(commandFilterInfo.getComponent(), parserList, params);
			if(commandFilterInfo.getClassType()!= null){
				log.warn("The component is used and the classtype is ignored for the commandFilter :" + commandFilterInfo.getFilterId());
			}
			return commandFilter;
		}
		
		if(commandFilterInfo.getClassType() != null){
			CommandFilter commandFilter = null;
			try{
				commandFilter = (CommandFilter)ClassUtil.createInstance(commandFilterInfo.getClassType(), new Class[]{List.class, Map.class}, parserList, params);
			}catch(RuntimeException exp){
				log.info(String.format("The java parser handler class doesn't have a contructor having List and Map as parameters: %s(List parserList, Map params)", commandFilterInfo.getClassType()), exp);
			}
			if(commandFilter == null){
				try{
					commandFilter = (CommandFilter)ClassUtil.createInstance(commandFilterInfo.getClassType(), new Class[]{List.class}, parserList);
				}catch(RuntimeException exp){
					log.info(String.format("The java parser handler class doesn't have a contructor having List as parameters: %s(List parserList)", commandFilterInfo.getClassType()), exp);
				}
			}
			if(commandFilter == null){			
				try{
					commandFilter = (CommandFilter)ClassUtil.createInstance(commandFilterInfo.getClassType(), new Class[]{Map.class}, params);
				}catch(RuntimeException exp){
					log.info(String.format("The java parser handler class doesn't have a contructor having Map as parameters: %s(Map params)", commandFilterInfo.getClassType()), exp);
				}
			}
			if(commandFilter == null){
				try{
					commandFilter = (CommandFilter)ClassUtil.createInstance(commandFilterInfo.getClassType());
				}catch(RuntimeException exp){
					log.info(String.format("The java parser handler class doesn't have a default contructor: %s()", commandFilterInfo.getClassType()), exp);
				}
			}

			return commandFilter;
		}
		ExceptionUtil.createRuntimeException("Cannot create the CommandFilter for filterId(%s)", commandFilterInfo.getFilterId());
		return null;
		
	}
	
	
	private static Map<String, String> convertParams(List<ParamInfo> params){
		HashMap<String, String> attributeMap = new HashMap<String, String>();
		if(params != null){
			for(ParamInfo param : params){
				attributeMap.put(param.getName(), param.getValue());
			}
		}
		return attributeMap;
	}
	
	public static ForEachTag createForEachTag(ForeachInfo foreachInfo){
		List<Tag> childrenTagList = new ArrayList<Tag>();
		
		if(foreachInfo.getTextOrForeach() != null){
			for(Object childObj : foreachInfo.getTextOrForeach()){
				if(childObj instanceof ForeachInfo){
					childrenTagList.add( createForEachTag((ForeachInfo)childObj));
					continue;
				}
				if(childObj instanceof TextInfo){
					childrenTagList.add( createTextTag((TextInfo)childObj));
					continue;
				}
				ExceptionUtil.createRuntimeException("Unrecognized tag in the foreach: " + childObj.toString());
			}
		}
		if(foreachInfo.getItems() == null){
			ExceptionUtil.createRuntimeException("The items in foreach tag cannot be null");
		}
		
		return new ForEachTag(foreachInfo.getVar(), foreachInfo.getItems(), foreachInfo.getRender(), childrenTagList);

	}

	public static TextTag createTextTag(TextInfo textInfo){
		return new TextTag(textInfo.getValue(), textInfo.getRender());
	}


	public static IResponseRender createResponseContent(String parentPath, ResponseInfo responseInfo){
		if(responseInfo == null) return null;
		
		List<IResponseRender> childRenderList = new ArrayList<IResponseRender>(); 
		
		List<Object> includeOrContentList = responseInfo.getIncludeOrTextOrForeach();
		for(Object includeOrContent : includeOrContentList){
			if(includeOrContent instanceof IncludeInfo){
				IncludeInfo includeInfo = (IncludeInfo) includeOrContent;
				IncludedRender includeRender = new IncludedRender(parentPath, includeInfo.getViewId());
				childRenderList.add(includeRender);
				continue;
			}
			
			if(includeOrContent instanceof ForeachInfo){
				
				ForEachTag foreachTag = createForEachTag((ForeachInfo) includeOrContent);
				childRenderList.add(new TagRender(foreachTag));
			}

			if(includeOrContent instanceof TextInfo){
				
				TextTag textTag = createTextTag((TextInfo) includeOrContent);
				childRenderList.add(new TagRender(textTag));
			}
		}

			
//				if(contentInfo.getType() != null){
//					if("text".equalsIgnoreCase(contentInfo.getType())){
//						new TextRender(contentInfo.getValue()));
//						
//					}else if("script".equalsIgnoreCase(contentInfo.getType())){
//						childRenderList.add(new ScriptRender(contentInfo.getValue()));
//					}else{
//						log.error("Cannot recognize the response render type:" + contentInfo.getType());
//					}
//					
//					if(contentInfo.getHanlder() != null){
//						log.warn(String.format("Type(%s) will be used for rendering response, and the handler(%s) is ignored", contentInfo.getType(), contentInfo.getHanlder()));
//					}
//
////					if(contentInfo.getExpression() != null){
////						log.warn(String.format("Handler(%s) will be used for rendering response, and the expression(%s) is ignored", contentInfo.getHanlder(), contentInfo.getExpression()));
////					}
//					continue;
//				}
//				if(contentInfo.getHanlder() != null){
//					try{
//						Class renderClazz = (Class) ClassUtil.loadClass(contentInfo.getHanlder());
//						IResponseRender render = (IResponseRender)renderClazz.getConstructor(String.class).newInstance(contentInfo.getValue());
//						childRenderList.add(render);
//					}catch(Exception exp){
//						ExceptionUtil.wrapRuntimeException("Cannot create response render handler instance for: " + contentInfo.getHanlder(), exp);
//					}
////					if(contentInfo.getExpression() != null){
////						log.warn(String.format("Handler(%s) will be used for rendering response, and the expression(%s) is ignored", contentInfo.getHanlder(), contentInfo.getExpression()));
////					}
//					if(!StringUtil.isNullOrEmptyAfterTrim(contentInfo.getValue())){
//						log.warn(String.format("Handler(%s) will be used for rendering response, and the content value is ignored", contentInfo.getHanlder()));
//					}
//					continue;
//				}
				
//				if(contentInfo.getExpression() != null){
//					ExpressionRender expressionRender = new ExpressionRender(contentInfo.getExpression());
//					childRenderList.add(expressionRender);
//					if(!StringUtil.isNullOrEmptyAfterTrim(contentInfo.getValue())){
//						log.warn(String.format("Expression(%s) will be used for rendering response, and the content value is ignored", contentInfo.getHanlder()));
//					}
//					continue;
//				}
				
//			}

//		}
		return new DefaultResponseRender(childRenderList); 
	}

}
