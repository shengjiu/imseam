package com.imseam.chatpage.impl;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IUserRequest;
import com.imseam.common.util.StringUtil;

public class RegexpParser extends AbstractPatternBasedParser {
	
	private static Log log = LogFactory.getLog(RegexpParser.class);

	private List<RegexpParseResultTemplate> resultTemplateList;
	private Pattern pattern = null; 
        
	
	public RegexpParser(String patternString, String id, boolean negative, String fireConditionExpression, List<RegexpParseResultTemplate> resultList){
		super(patternString,id, negative, fireConditionExpression);
		this.resultTemplateList = resultList;
		this.pattern = Pattern.compile(getPatternString(), Pattern.CASE_INSENSITIVE);;
	}
	
	public RegexpParser(String patternString, List<RegexpParseResultTemplate> resultList){
		this(patternString, null, false, null, resultList);
	}

	
	public boolean parseInputOnly(String input, IUserRequest request) {
		
		assert(input != null);
		assert(request != null);
		
        Matcher matcher = pattern.matcher(input);
        boolean found = matcher.find();

        if(found){
            log.debug(String.format("I found the text \"%s\" starting at index %d and ending at index %d.%n",
                    matcher.group(), matcher.start(), matcher.end()));
            
            if(resultTemplateList != null && resultTemplateList.size() > 0){
            	log.debug("group count:" +matcher.groupCount());
            	for(RegexpParseResultTemplate template : resultTemplateList){
            		String groupMatchResult = matcher.group(template.getGroupNumber());
            		if(groupMatchResult == null){
            			log.warn("The parse result doesn't have expected group");
            			continue;
            		}
            		assert(!StringUtil.isNullOrEmptyAfterTrim(template.getParameterName()));
            		request.setParameter(template.getParameterName(), groupMatchResult);
            	}
            }
        }
        
        return found; 
	}

	
	static public class RegexpParseResultTemplate{
		
		private int groupNumber;
		private String parameterName;
		
		public RegexpParseResultTemplate(int groupNumber, String parameterName){
			assert(groupNumber >= 0);
			assert(!StringUtil.isNullOrEmptyAfterTrim(parameterName));
			this.groupNumber = groupNumber;
			this.parameterName = parameterName;
		}
		
		public int getGroupNumber() {
			return groupNumber;
		}
		public String getParameterName() {
			return parameterName;
		}
	}
	
}
