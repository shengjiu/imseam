package com.imseam.chatlet.config.util;

import java.util.HashMap;

import com.imseam.chatlet.config.Param;
import com.imseam.chatlet.config.Params;

public class ConfigUtil {
	
	static public HashMap<String, String> convertParams(Params params)
	{
		if(params == null) return null;
		HashMap<String, String> attributeMap = new HashMap<String, String>();
		for(Param param : params.getParam())
		{
			attributeMap.put(param.getName(), param.getValue());
		}
		return attributeMap;
	}

}
