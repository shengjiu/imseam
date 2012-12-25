package com.imseam.chatpage;

import java.util.Map;

import com.imseam.chatlet.IUserRequest;

public interface IParser {
	
	ParseResult parseInput(String input, IUserRequest request);
	
	String getId();
	
	String getFireConditionExpression();
	
	public class ParseResult{
		private boolean recognized;
		
		//right now the parameterMap is not used
		private Map<String, String> parameterMap;
		
		public ParseResult(boolean recognized, Map<String, String> parameterMap){
			this.recognized = recognized;
			this.parameterMap = parameterMap;
		}

		public static ParseResult unRecognized(){
			return new ParseResult(false, null);
		}

		public static ParseResult recognized(){
			return new ParseResult(true, null);
		}
		
		public void setRecognized(boolean recognized){
			this.recognized = recognized;
		}
		
		public Map<String, String> getParameterMap(){
			return this.parameterMap;
		}
		
		public boolean isRecognized(){
			return recognized;
		}
		
	}
}
