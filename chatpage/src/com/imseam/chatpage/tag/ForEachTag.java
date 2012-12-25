package com.imseam.chatpage.tag;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.el.VariableMapper;

import com.imseam.chatpage.context.ChatpageContext;
import com.imseam.chatpage.el.IndexedValueExpression;
import com.imseam.chatpage.el.IteratedValueExpression;
import com.imseam.chatpage.el.MappedValueExpression;
import com.imseam.chatpage.impl.ItemIteratorUtil;

public class ForEachTag extends Tag {

	private String varString = null;
	private String itemsString = null;

	private List<Tag> childrenTagList = null;
	
	private static String FOR_EACH_ITEMS_VALUE_EXPRESSION = ForEachTag.class.getName() + "FOR_EACH_ITEMS_VALUE_EXPRESSION"; 

//	private static String FOR_EACH_ITEMS_OBJECT = ForEachTag.class.getName() + "FOR_EACH_TAG_ITEMS_OBJECT"; 
	
	public ForEachTag(String var, String items, String renderExpression, List<Tag> childrenTagList) {
		super(renderExpression);
		assert (items != null);

		this.itemsString = items;
		this.varString = var;
		this.childrenTagList = childrenTagList;
	}

	private final ValueExpression capture(String name, VariableMapper vars) {
		if (name != null) {
			return vars.setVariable(name, null);
		}
		return null;
	}

	private final ValueExpression getVarExpr(ValueExpression ve, Object src, Object value, int i) {
		if (src instanceof List || src.getClass().isArray()) {
			return new IndexedValueExpression(ve, i);
		} else if (src instanceof Map && value instanceof Map.Entry) {
			return new MappedValueExpression(ve, (Map.Entry<?,?>) value);
		} else if (src instanceof Collection) {
			return new IteratedValueExpression(ve, value);
		}
		throw new IllegalStateException("Cannot create VE for: " + src);
	}

	protected int getBegin(ChatpageContext context) {
		return 0;
	}

	protected int getPageSize(ChatpageContext context) {
		return -1;
	}

	protected Object getForEachItemsObject(){
		return null;
	}
	
	private ValueExpression getForEachItemsValueExpression(ChatpageContext context){
		Object itemsVEObject = ChatpageContext.current().getAttribute(FOR_EACH_ITEMS_VALUE_EXPRESSION);
		
		if(itemsVEObject == null) {
			itemsVEObject = context.getExpressionFactory().createValueExpression(ChatpageContext.current().getELContext(), itemsString, Object.class);
			ChatpageContext.current().setAttribute(FOR_EACH_ITEMS_VALUE_EXPRESSION, itemsVEObject);
		}
		
		return (ValueExpression) itemsVEObject;
	}
	
	
	
	public Object calculateItemsObject(ChatpageContext context){
		ELContext elContext = context.getELContext();

		ValueExpression itemsVE = context.getExpressionFactory().createValueExpression(elContext, itemsString, Object.class);
		
		ChatpageContext.current().setAttribute(FOR_EACH_ITEMS_VALUE_EXPRESSION, itemsVE);

		Object itemsObject = itemsVE.getValue(elContext);
		return itemsObject;
	}
	
	protected void renderBullet(ChatpageContext context, int i){
		context.getWindow().getMessageSender().send(i + ". ");
	}

	@Override
	protected void renderTag(ChatpageContext context) {

		ELContext elContext = context.getELContext();
		
		Object itemsObj = this.getForEachItemsObject();
		
		if(itemsObj == null){
			itemsObj = calculateItemsObject(context);
		}
		
		ValueExpression itemsVE = this.getForEachItemsValueExpression(context);
		assert(itemsVE != null);

		Iterator<?> itr = ItemIteratorUtil.toIterator(itemsObj);

		if (itr == null) {
			return;
		}

		VariableMapper varMap = elContext.getVariableMapper();

		ValueExpression varExpressionOriginal = this.capture(varString, varMap);

		int i = 0;
		int count = 0;
		int size = getPageSize(context);
		int begin = this.getBegin(context);
		try {
			while (itr.hasNext()) {
				Object value = itr.next();

				if (i < begin) {
					i++;
					continue;
				}

				if (size > 0 && count++ >= size) {
					break;
				}

				if (varString != null) {
					ValueExpression varExpression = this.getVarExpr(itemsVE, itemsObj, value, i);
					varMap.setVariable(varString, varExpression);
				}
				
				renderBullet(context, i + 1);
				
				renderChildren(context);
				
				i++;
			}
		} finally {
			if (varString != null) {
				varMap.setVariable(varString, varExpressionOriginal);
			}
		}
	}
	
	
	protected void renderChildren(ChatpageContext context){
		if (childrenTagList != null) {
			for (Tag tag : childrenTagList) {
				tag.render(context);
			}
		}
	}


	
}
