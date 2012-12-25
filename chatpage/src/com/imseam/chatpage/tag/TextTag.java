package com.imseam.chatpage.tag;

import com.imseam.chatpage.context.ChatpageContext;
import com.imseam.chatpage.el.ELUtil;

public class TextTag extends Tag {

	private String text;
	
	private boolean isLiteralText = false;

	

	public TextTag(String text, String renderExpression) {
		super(renderExpression);

		this.text = text;
		isLiteralText = ELUtil.isLiteralText(text);
	}


	@Override
	protected void renderTag(ChatpageContext context) {
		if(isLiteralText) { 
			context.sendPlainString(text);
			return;
		}
		
		context.sendELString(text);

	}

}
