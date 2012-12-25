package com.imseam.chatpage.config.test;

import java.util.List;

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
import com.imseam.chatpage.config.ParamsInfo;
import com.imseam.chatpage.config.ParserRefInfo;
import com.imseam.chatpage.config.ParsersInfo;
import com.imseam.chatpage.config.RegexpParserInfo;
import com.imseam.chatpage.config.RegexpParserInfo.RegexpParseresultInfo;
import com.imseam.chatpage.config.ResponseInfo;
import com.imseam.chatpage.config.ResponseInfo.IncludeInfo;
import com.imseam.chatpage.config.StringParserInfo;
import com.imseam.chatpage.config.TextInfo;
import com.imseam.chatpage.config.util.ChatPageConfigReader;



public class MarshallerTest {

	/**
	 * @param args
	 */
	@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
	public static void main(String[] args) {
		
		ParamInfo param1 = new ParamInfo();
		param1.setName("param1");
		param1.setValue("value1");
		ParamInfo param2 = new ParamInfo();
		param2.setName("param2");
		param2.setValue("value2");
		ParamsInfo paramsInfo = new ParamsInfo();
		paramsInfo.getParam().add(param1);
		paramsInfo.getParam().add(param2);

		
		ChatPagesInfo pages = new ChatPagesInfo();
		List chatPageOrGlobalParsers = pages.getChatPageOrMenuPageOrGlobalParsers();
		ChatPageInfo page1 = new ChatPageInfo();
		ResponseInfo rc1_body = new ResponseInfo();
		List includeOrContentList = rc1_body.getIncludeOrTextOrForeach();
		IncludeInfo rc1_body_include1 = new IncludeInfo();
		rc1_body_include1.setViewId("include view page: rc1_body_include1");
		
		List<Object> rc1_body_content_list  = rc1_body.getIncludeOrTextOrForeach();
		
		TextInfo rc1_body_content_text1 = new TextInfo();
		rc1_body_content_text1.setValue("rc1_body_content_text1_value");
		rc1_body_content_text1.setRender("text rendered");
		
		includeOrContentList.add(rc1_body_content_text1);
		
		ForeachInfo foreach = new ForeachInfo();
		foreach.setItems("foreach item");
		foreach.setVar("foreach var");
		foreach.setRender("render");
		TextInfo foreach_text1 = new TextInfo();
		foreach_text1.setValue("foreach_text1_value");
		foreach_text1.setRender("text rendered");
		foreach.getTextOrForeach().add(foreach_text1);
		
		includeOrContentList.add(foreach);
		
		TextInfo rc1_body_content_text2 = new TextInfo();
		rc1_body_content_text2.setValue("rc1_body_content_text2_value");
		includeOrContentList.add(rc1_body_content_text2);

		IncludeInfo rc1_body_include2 = new IncludeInfo();
		rc1_body_include2.setViewId("include view page: rc1_body_include2");
		page1.setParams(paramsInfo);
		
		includeOrContentList.add(rc1_body_include1);
		includeOrContentList.add(rc1_body_include2);
		
		ResponseInfo rc1_help = new ResponseInfo();
		
		includeOrContentList = rc1_help.getIncludeOrTextOrForeach();
		IncludeInfo rc1_help_include1 = new IncludeInfo();
		rc1_help_include1.setViewId("include view page: rc1_help_include1");
		IncludeInfo rc1_help_include2 = new IncludeInfo();
		rc1_help_include2.setViewId("include view page: rc1_help_include2");
		
		includeOrContentList.add(rc1_help_include1);
		includeOrContentList.add(rc1_help_include2);
		

		page1.setViewId("/page1");
		page1.setBody(rc1_body);
		page1.setHelp(rc1_help);
		ParsersInfo parsers = new ParsersInfo();
		List parserList = parsers.getRegexpParserOrJavaParserOrStringParser();
		RegexpParserInfo rc1_parser1 = new RegexpParserInfo();
		ActionInfo action1 = new ActionInfo();
		action1.setClazz("action.class.1");
		action1.setMethod("method1");
		rc1_parser1.setPattern("page1.Yes|Y");
		RegexpParseresultInfo rc1_parser1_result1 = new RegexpParseresultInfo();
		rc1_parser1_result1.setGroupNumber((byte)1);
		rc1_parser1_result1.setParameterName("rc1_parser1_result1_g1");
		rc1_parser1.getRegexpParseresult().add(rc1_parser1_result1);
		RegexpParseresultInfo rc1_parser1_result2 = new RegexpParseresultInfo();
		rc1_parser1_result2.setGroupNumber((byte)2);
		rc1_parser1_result2.setParameterName("rc1_parser1_result1_g2");
		rc1_parser1.getRegexpParseresult().add(rc1_parser1_result2);	
		
		parsers.getRegexpParserOrJavaParserOrStringParser().add(rc1_parser1);
		
		RegexpParserInfo rc1_parser2 = new RegexpParserInfo();
		rc1_parser2.setPattern("page1.No|N");
		JavaParserInfo rc1_parser3 = new JavaParserInfo();
		ParamsInfo paramsInfo1 = new ParamsInfo();
		rc1_parser3.setParams(paramsInfo1);
		List<ParamInfo> paramList = rc1_parser3.getParams().getParam();
		paramList.add(param1);
		paramList.add(param2);

		
		rc1_parser3.setHandler("page1.java.parser.handler");
		ParserRefInfo rc1_parser4 = new ParserRefInfo();
		
		
		StringParserInfo rc1_parser5 = new StringParserInfo();
		
		rc1_parser5.setPattern("string|parser");
		
		ActionsInfo pageActions1 = new ActionsInfo();
		ActionInfo pageAction1 = new ActionInfo();
		pageAction1.setClazz("pageAction.class");
		pageAction1.setOutcome("pageAction.outcome");
		ParsersInfo parsersInfo1 = new ParsersInfo();
		pageAction1.setParsers(parsersInfo1);
		pageAction1.getParsers().getRegexpParserOrJavaParserOrStringParser().add(rc1_parser1);
		pageAction1.getParsers().getRegexpParserOrJavaParserOrStringParser().add(rc1_parser2);
		pageAction1.getParsers().getRegexpParserOrJavaParserOrStringParser().add(rc1_parser3);
		pageAction1.getParsers().getRegexpParserOrJavaParserOrStringParser().add(rc1_parser4);
		pageAction1.getParsers().getRegexpParserOrJavaParserOrStringParser().add(rc1_parser5);
		
		pageActions1.getAction().add(pageAction1);
		page1.setActions(pageActions1);		
		

		
		ChatPageInfo page2 = new ChatPageInfo();
		ResponseInfo rc2_body = new ResponseInfo();
		
		includeOrContentList = rc2_body.getIncludeOrTextOrForeach();
		IncludeInfo rc2_body_include1 = new IncludeInfo();
		rc2_body_include1.setViewId("include view page: rc2_body_include1");
		TextInfo rc2_body_content_text1 = new TextInfo();
		rc2_body_content_text1.setValue("rc2_body_content_text1_value");
		includeOrContentList.add(rc2_body_content_text1);
		
		IncludeInfo rc2_body_include2 = new IncludeInfo();
		rc2_body_include2.setViewId("include view page: rc2_body_include2");
		
		includeOrContentList.add(rc2_body_include1);
		includeOrContentList.add(rc2_body_include2);		
		

		ResponseInfo rc2_help = new ResponseInfo();

		includeOrContentList = rc2_help.getIncludeOrTextOrForeach();
		IncludeInfo rc2_help_include1 = new IncludeInfo();
		rc2_help_include1.setViewId("include view page: rc2_help_include1");
		IncludeInfo rc2_help_include2 = new IncludeInfo();
		rc2_help_include2.setViewId("include view page: rc2_help_include2");
		
		includeOrContentList.add(rc2_help_include1);
		includeOrContentList.add(rc2_help_include2);			
		

		page2.setViewId("/page2");
		page2.setBody(rc2_body);
		page2.setHelp(rc2_help);
		ParsersInfo parsers2 = new ParsersInfo();
		List parserList2 = parsers2.getRegexpParserOrJavaParserOrStringParser();
		RegexpParserInfo rc2_parser1 = new RegexpParserInfo();
		rc2_parser1.setPattern("page2.Yes|Y");
		
		RegexpParserInfo rc2_parser2 = new RegexpParserInfo();
		rc2_parser2.setPattern("page2.No|N");
		JavaParserInfo rc2_parser3 = new JavaParserInfo();
		rc2_parser3.setHandler("page2.java.parser.handler");
		ParserRefInfo rc2_parser4 = new ParserRefInfo();
		
		page2.setParams(paramsInfo);

		ActionsInfo pageActions2 = new ActionsInfo();
		ActionInfo pageAction2 = new ActionInfo();
		pageAction2.setClazz("pageAction.class");
		pageAction2.setOutcome("pageAction.outcome");
		
		ParsersInfo parsersInfo2 = new ParsersInfo();
		pageAction2.setParsers(parsersInfo2);	
		pageAction2.getParsers().getRegexpParserOrJavaParserOrStringParser().add(rc2_parser1);
		pageAction2.getParsers().getRegexpParserOrJavaParserOrStringParser().add(rc2_parser2);
		pageAction2.getParsers().getRegexpParserOrJavaParserOrStringParser().add(rc2_parser3);
		pageAction2.getParsers().getRegexpParserOrJavaParserOrStringParser().add(rc2_parser4);
		
		pageActions2.getAction().add(pageAction2);
		page2.setActions(pageActions2);
		GlobalParsersInfo globalParsers = new GlobalParsersInfo();
		List parserList_g = globalParsers.getRegexpParserOrJavaParserOrStringParser();
		
		
		RegexpParserInfo g_parser1 = new RegexpParserInfo();
		g_parser1.setPattern("g.Yes|Y");
		RegexpParserInfo g_parser2 = new RegexpParserInfo();
		g_parser2.setPattern("g.No|N");
		JavaParserInfo g_parser3 = new JavaParserInfo();
		g_parser3.setHandler("g.java.parser.handler");

		parserList_g.add(g_parser1);
		parserList_g.add(g_parser2);
		parserList_g.add(g_parser3);
		
		GroupedParserInfo parsergroup = new GroupedParserInfo();
		parsergroup.setName("parsergroup");
		RegexpParserInfo group_parser1 = new RegexpParserInfo();
		group_parser1.setPattern("group.Yes|Y");
		RegexpParserInfo group_parser2 = new RegexpParserInfo();
		group_parser2.setPattern("group.No|N");
		JavaParserInfo group_parser3 = new JavaParserInfo();
		group_parser3.setHandler("group.java.parser.handler");
		parsergroup.getRegexpParserOrJavaParserOrStringParser().add(group_parser1);
		parsergroup.getRegexpParserOrJavaParserOrStringParser().add(group_parser2);
		parsergroup.getRegexpParserOrJavaParserOrStringParser().add(group_parser3);
		
		parserList_g.add(parsergroup);
		
		pages.setName("pageS name, may not useful");
		chatPageOrGlobalParsers.add(page1);
		
		MenuPageInfo menupage1 = new MenuPageInfo();
		menupage1.setPageSize("10");
		menupage1.setMenuSelectedAction("item selected action");
		menupage1.setItems("items expresion");
		menupage1.setVar("var");
		menupage1.setViewId("menu page1 view id");
		
		
		ResponseInfo menuHeader = new ResponseInfo();
		includeOrContentList = menuHeader.getIncludeOrTextOrForeach();
		IncludeInfo menuHeader_include1 = new IncludeInfo();
		menuHeader_include1.setViewId("include view page: menu_header_include1");
		includeOrContentList.add(menuHeader_include1);
		TextInfo menuHeader_content_text1 = new TextInfo();
		menuHeader_content_text1.setValue("menuHeader_content_text1_value");
		includeOrContentList.add(menuHeader_content_text1);
		menupage1.setHeader(menuHeader);

		ResponseInfo menuItems = new ResponseInfo();
		includeOrContentList = menuItems.getIncludeOrTextOrForeach();
		IncludeInfo menuItems_include1 = new IncludeInfo();
		menuItems_include1.setViewId("include view page: menu_items_include1");
		includeOrContentList.add(menuItems_include1);
		TextInfo menuItems_content_text1 = new TextInfo();
		menuItems_content_text1.setValue("menuItems_content_text1_value");
		includeOrContentList.add(menuItems_content_text1);
		menupage1.setMenuItem(menuItems);
		
		ResponseInfo menufooter = new ResponseInfo();
		includeOrContentList = menufooter.getIncludeOrTextOrForeach();
		IncludeInfo menufooter_include1 = new IncludeInfo();
		menufooter_include1.setViewId("include view page: menu_footer_include1");
		includeOrContentList.add(menufooter_include1);
		TextInfo menufooter_content_text1 = new TextInfo();
		menufooter_content_text1.setValue("menufooter_content_text1_value");
		includeOrContentList.add(menufooter_content_text1);
		menupage1.setFooter(menufooter);

		
		ParsersInfo menupage1parsers = new ParsersInfo();
		List menupage1ParserList = parsers2.getRegexpParserOrJavaParserOrStringParser();
		RegexpParserInfo menupage1_parser1 = new RegexpParserInfo();
		menupage1_parser1.setPattern("page2.Yes|Y");
		
		RegexpParserInfo menupage1_parser2 = new RegexpParserInfo();
		menupage1_parser2.setPattern("page2.No|N");
		JavaParserInfo menupage1_parser3 = new JavaParserInfo();
		menupage1_parser3.setHandler("page2.java.parser.handler");
		ParserRefInfo menupage1_parser4 = new ParserRefInfo();
		
		menupage1.setParams(paramsInfo);

		ActionsInfo menupage1Actions = new ActionsInfo();
		ActionInfo menupage1Action = new ActionInfo();
		menupage1Action.setClazz("pageAction.class");
		menupage1Action.setOutcome("pageAction.outcome");
		
		ParsersInfo menupage1ParsersInfo = new ParsersInfo();
		menupage1Action.setParsers(menupage1parsers);	
		menupage1Action.getParsers().getRegexpParserOrJavaParserOrStringParser().add(menupage1_parser1);
		menupage1Action.getParsers().getRegexpParserOrJavaParserOrStringParser().add(menupage1_parser2);
		menupage1Action.getParsers().getRegexpParserOrJavaParserOrStringParser().add(menupage1_parser3);
		menupage1Action.getParsers().getRegexpParserOrJavaParserOrStringParser().add(menupage1_parser4);
		
		menupage1Actions.getAction().add(menupage1Action);
		menupage1.setActions(menupage1Actions);		
		
		
		
		chatPageOrGlobalParsers.add(menupage1);
		

		CommandFilterInfo filter1 = new CommandFilterInfo();
		filter1.setClassType("filter1-class-type");
		filter1.setFilterId("filter1");
		filter1.setComponent("filter1-component");
		filter1.setParsers(parsers);

		chatPageOrGlobalParsers.add(filter1);
		
		chatPageOrGlobalParsers.add(page2);

		CommandFilterInfo filter2 = new CommandFilterInfo();
		filter2.setClassType("filter2-class-type");
		filter2.setFilterId("filter2");
		filter2.setComponent("filter2-component");
		filter2.setParsers(parsers);
		filter2.setParams(paramsInfo);
		
		chatPageOrGlobalParsers.add(filter2);
		
		chatPageOrGlobalParsers.add(globalParsers);
		
		CommandFilterMappingInfo mapping1 = new CommandFilterMappingInfo();
		mapping1.setFilterId("mapping1-filter1");
		mapping1.setPathPattern("mapping1-path-pattern*");
		
		chatPageOrGlobalParsers.add(mapping1);
		
		CommandFilterMappingInfo mapping2 = new CommandFilterMappingInfo();
		mapping2.setFilterId("mapping2-filter1");
		mapping2.setPathPattern("mapping2-path-pattern*");
		
		chatPageOrGlobalParsers.add(mapping2);
		
		
		
		System.out.println(ChatPageConfigReader.marshallChatPagesConfig(pages));

	}

}
