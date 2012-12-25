package com.imseam.chatpage.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IMessageSender;
import com.imseam.chatlet.IUserRequest;
import com.imseam.chatpage.IChatPage;
import com.imseam.common.util.ExceptionUtil;
import com.imseam.common.util.StringUtil;

public class CommandFilterManager {
	
	private static Log log = LogFactory.getLog(CommandFilterManager.class);
	private static CommandFilterManager instance = new CommandFilterManager();
	
	private List<CommandFilterMapping> filterMappingList = new ArrayList<CommandFilterMapping>();
	private Map<String, CommandFilter> filterMap = new HashMap<String, CommandFilter>();
	private Map<String, CommandFilterChain> filterChainCache = new ConcurrentHashMap<String, CommandFilterChain>();
	
	private CommandFilterManager(){
		
	}
	
	public static CommandFilterManager getInstance(){
		return instance;
	}
	
	public void addFilter(String filterId, CommandFilter filter){
		assert(!StringUtil.isNullOrEmptyAfterTrim(filterId));
		assert(filter != null);
		if(filterMap.get(filterId) != null){
			ExceptionUtil.createRuntimeException("The filter with Id, %s, already exist.", filterId);
			return;
		}
		this.filterMap.put(filterId, filter);
	}
	
	public void addFilterMapping(String filterId, String pathPattern){
		if(filterMap.get(filterId) == null){
			log.warn(String.format("The filter with Id, %s, doesn't exist. Please put the filterConfigure ahead of the filter mapping", filterId));
			return;
		}
		this.filterMappingList.add(new CommandFilterMapping(filterId, pathPattern));
	}
	
	
	
	public void doCommandFilter(IChatPage chatpage, IUserRequest request, String input, IMessageSender responseSender, CommandFilterChainCallback callback){
		String requestPath = chatpage.getFullPathViewID();
		CommandFilterChain filterChain = filterChainCache.get(requestPath);
		if(filterChain == null){
			ArrayList<CommandFilter> filterList = new ArrayList<CommandFilter>();
			for(CommandFilterMapping filterMapping: filterMappingList){
				if(matchFiltersURL(filterMapping.getPathPattern(), requestPath)){
					CommandFilter filter = filterMap.get(filterMapping.getFilterId());
					assert(filter != null);
					filterList.add(filter);
				}
			}
			filterChain = new DefaultCommandFilterChain(filterList, callback);
			filterChainCache.put(requestPath, filterChain);
		}
		filterChain.doCommandFilter(chatpage, request,input, responseSender);
	}
	
	
	
    /**
     * Return <code>true</code> if the context-relative request path
     * matches the requirements of the specified filter mapping;
     * otherwise, return <code>null</code>.
     *
     * @param filterMap Filter mapping being checked
     * @param requestPath Context-relative request path of this request
     */
    private boolean matchFiltersURL(String pattern, String requestPath) {
    	
    	assert(!StringUtil.isNullOrEmptyAfterTrim(pattern));
    	assert(!StringUtil.isNullOrEmptyAfterTrim(requestPath));

        if (requestPath == null)
            return (false);

        // Case 1 - Exact Match
        if (pattern.equals(requestPath))
            return (true);

        // Case 2 - Path Match ("/.../*")
        if (pattern.equals("/*"))
            return (true);
        if (pattern.endsWith("/*")) {
            if (pattern.regionMatches(0, requestPath, 0, 
                                       pattern.length() - 2)) {
                if (requestPath.length() == (pattern.length() - 2)) {
                    return (true);
                } else if ('/' == requestPath.charAt(pattern.length() - 2)) {
                    return (true);
                }
            }
            return (false);
        }

        // Case 3 - Extension Match
        if (pattern.startsWith("*.")) {
            int slash = requestPath.lastIndexOf('/');
            int period = requestPath.lastIndexOf('.');
            if ((slash >= 0) && (period > slash) 
                && (period != requestPath.length() - 1)
                && ((requestPath.length() - period) 
                    == (pattern.length() - 1))) {
                return (pattern.regionMatches(2, requestPath, period + 1,
                                               pattern.length() - 2));
            }
        }

        // Case 4 - "Default" Match
        return (false); // NOTE - Not relevant for selecting filters
    }
    
}
