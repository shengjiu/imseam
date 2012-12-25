package com.imseam.chatpage.tag;

import java.util.ArrayList;
import java.util.List;

import javax.el.ValueExpression;

import com.imseam.chatpage.context.ChatpageContext;

public abstract class Tag {

	private static final String TAG_STACK = "tagStack:" + Tag.class.getName();

	public static final String CURRENT_TAG = "CURRENT_TAG" + Tag.class.getName();

	private String renderExpression = null;

	private Boolean renderBoolean = null;
	
	protected Tag(String renderExpression){
		renderExpression = renderExpression == null? null : renderExpression.trim();
		
		this.renderExpression = renderExpression;
		
		if(renderExpression == null || "true".equalsIgnoreCase(renderExpression)){
			renderBoolean = Boolean.TRUE;
		}
		if("false".equalsIgnoreCase(renderExpression)){
			renderBoolean = Boolean.FALSE;		
		}
	}

	public void render(ChatpageContext context) {
		if(!isRender(context)){
			return;
		}
		this.pushTagToEL(context, this);
		
		renderTag(context);
		
		popTagFromEL(context);
	}
	
	

	protected abstract void renderTag(ChatpageContext context);

	
	public boolean isRender(ChatpageContext context) {
		if (renderBoolean != null) {
			return renderBoolean;
		}
		
		ValueExpression ve = context.getExpressionFactory().createValueExpression(context.getELContext(), renderExpression, Boolean.class);


		return Boolean.valueOf(ve.getValue(context.getELContext()).toString());
	}

	public static Tag getCurrentTag(ChatpageContext context) {

		Object currentTagObject = context.getAttribute(CURRENT_TAG);

		if (currentTagObject != null) {
			return (Tag) context.getAttribute(CURRENT_TAG);
		} else {
			@SuppressWarnings("unchecked")
			List<Tag> tagStack = (List<Tag>) context.getAttribute(TAG_STACK);
			if (tagStack != null && tagStack.size() > 0) {
				return tagStack.get(tagStack.size() - 1);
			}
		}
		return null;
	}

	public final void popTagFromEL(ChatpageContext context) {
		@SuppressWarnings("unchecked")
		List<Tag> tagStack = (List<Tag>) context.getAttribute(TAG_STACK);

		Tag oldCurrent = (Tag) context.getAttribute(CURRENT_TAG);

		Tag newCurrent = null;
		if (tagStack != null && !tagStack.isEmpty()) {

			if (this.equals(oldCurrent)) {
				newCurrent = tagStack.remove(tagStack.size() - 1);
			} else {
				// Check on the tag Stack if it can be found
				int tagIndex = tagStack.lastIndexOf(this);
				if (tagIndex >= 0) {
					// for (int i = 0; i < (tagIndex + 1); i++)
					for (int i = tagStack.size() - 1; i >= tagIndex; i--) {
						newCurrent = tagStack.remove(tagStack.size() - 1);

					}
				} else {
					// Tag not found on the stack. Do not pop.
					return;
				}
			}

			context.setAttribute(CURRENT_TAG, newCurrent);

		}
	}

	@SuppressWarnings("unchecked")
	public final void pushTagToEL(ChatpageContext context, Tag tag) {
		if (tag == null) {
			tag = this;
		}

		Tag currentTag = (Tag) context.getAttribute(CURRENT_TAG);

		if (currentTag != null) {
			List<Tag> tagStack = (List<Tag>) context.getAttribute(TAG_STACK);
			if (tagStack == null) {
				tagStack = new ArrayList<Tag>();
				context.setAttribute(TAG_STACK, tagStack);
			}

			tagStack.add(currentTag);
		}

		// Push the current UITag this to the FacesContext attribute map
		// using the key CURRENT_TAG
		// saving the previous UITag associated with CURRENT_TAG for
		// a subsequent call to
		// popTagFromEL(javax.faces.context.FacesContext).
		context.setAttribute(CURRENT_TAG, tag);

	}
	
}
