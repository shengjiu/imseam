package com.imseam.chatpage.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Logger;

import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ELContext;
import javax.el.ELContextEvent;
import javax.el.ELContextListener;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.ResourceBundleELResolver;
import javax.el.ValueExpression;

import org.jboss.el.ExpressionFactoryImpl;

import com.imseam.chatlet.IApplication;
import com.imseam.chatlet.IAttributes;
import com.imseam.chatlet.IChannel;
import com.imseam.chatlet.IConnection;
import com.imseam.chatlet.IMessageSender;
import com.imseam.chatlet.ISession;
import com.imseam.chatlet.IWindow;
import com.imseam.chatpage.ChatPageManager;
import com.imseam.chatpage.el.ChatpageELContext;
import com.imseam.chatpage.el.ScopedAttributeResolver;
import com.imseam.chatpage.el.implicitobject.ImplicitObjectResolver;
import com.imseam.chatpage.impl.ChatPageData;



public class ChatpageContext implements IAttributes {
	
	private static final Logger log = Logger.getLogger(ChatpageContext.class.getName());
    
	private static final String CHAT_PAGE_DATA_QUEUE_KEY = ChatpageContext.class + "CHAT_PAGE_DATA_QUEUE_KEY";
	
	private static final String CURRENT_CHAT_PAGE_DATA_KEY = ChatpageContext.class + "CURRENT_CHAT_PAGE_DATA_KEY";
	
	private static ThreadLocal<ChatpageContext> currentInstance = new ThreadLocal<ChatpageContext>();
	
    protected boolean released = false;
    
    private IWindow window = null;
    
    private IChannel channel = null;
    
    private IAttributes request = null;
    
    private ELContext elContext = null;
    
    private ChatPageData currentPageData = null;
    
	private Map<String, Object> attributeMap = new HashMap<String, Object>();
	
	private static ExpressionFactory expressionFactory = new ExpressionFactoryImpl();
	
	private static ELResolver elResolver = null;
	
	private static List<ELResolver> externalELResolverList = new ArrayList<ELResolver>();
	
	private Queue<ChatPageData> queue = null;
	
    public static ChatpageContext current(){
        return currentInstance.get();
    }
    
	public ChatpageContext(IAttributes event, IWindow targetWindow){
    	request = event;
    	currentInstance.set(this);
    	setTargetWindow(targetWindow);
    }
	
	public ChatpageContext(IAttributes event, IChannel channel){
    	request = event;
    	currentInstance.set(this);
    	setChannel(channel);
    }

	public ChatpageContext(IAttributes event){
    	request = event;
    	currentInstance.set(this);
    }

	
	/*
	 * If the targetWindow is null, set the target window will be required
	 */
	public void setTargetWindow(IWindow targetWindow){
		assert(targetWindow != null);
		this.window = targetWindow;
    	Object currentPageDataObject = targetWindow.getAttribute(CURRENT_CHAT_PAGE_DATA_KEY);
    	if(currentPageDataObject != null){
    		currentPageData = (ChatPageData) targetWindow.getAttribute(CURRENT_CHAT_PAGE_DATA_KEY);
    	}
	}

	
	public void setChannel(IChannel channel){
		assert(channel != null);
		this.channel = channel;
		setTargetWindow(channel.getWindow());
	}
	
	public ChatPageData getCurrentPageData() {
		return currentPageData;
	}
	
	public void setCurrentPageData(ChatPageData currentPageData){
		this.currentPageData = currentPageData;
	}
	
	public void saveCurrentPageDataToWindow(){
		this.window.setAttribute(CURRENT_CHAT_PAGE_DATA_KEY, currentPageData);
	}
	
	
	@SuppressWarnings("unchecked")
	private Queue<ChatPageData> getChatPageDataQueue(){
		if(queue != null) return queue;
		
    	Object queueObject = this.window.getAttribute(CHAT_PAGE_DATA_QUEUE_KEY);
    	
    	if(queueObject == null){
    		 queue = new LinkedList<ChatPageData>();
    	}else{
    		queue = (Queue<ChatPageData>) queueObject;
    	}
    	return queue;
		
	}
	
	public void pushToPageQueue(ChatPageData pageData){
		this.getChatPageDataQueue().add(pageData);
	}
	
	public ChatPageData peekPageQueue(){
		return this.getChatPageDataQueue().peek();
	}
	
	public ChatPageData popPageQueue(){
		return this.getChatPageDataQueue().poll();
	}
    
	public Object getAttribute(String name) {
		return attributeMap.get(name);
	}

	public Set<String> getAttributeNames() {
		return attributeMap.keySet();
	}

	public Object removeAttribute(String name) {
		return attributeMap.remove(name);
	}

	public void setAttribute(String name, Object o) {
		attributeMap.put(name, o);
	}

	public void removeAllAttributes() {
		attributeMap.clear();
	}
        

    public IApplication getApplication(){
    	return window.getConnection().getApplication();
    }
    
    public IWindow getWindow() {
		return window;
	}

	public IChannel getChannel() {
		return channel != null ? channel : window.getDefaultChannel();
	}
	
	public IConnection getConnection(){
		return window.getConnection();
	}
	
	public ISession getSession(){
		return getChannel().getUserSession();
	}
    
	public IAttributes getRequest(){
		return request;
	}

	public IMessageSender getMessengeSender() {
		return this.getWindow().getMessageSender();
	}

	private ELResolver getELResolver(){
		if(elResolver == null){
			elResolver = buildELResolver();
		}
		return elResolver;
	}
	
    public ELContext getELContext()
    {

        if (elContext != null)
        {
            return elContext;
        }

        elContext = new ChatpageELContext(getELResolver(), this);

        ELContextEvent event = new ELContextEvent(elContext);
        for (ELContextListener listener : ChatPageManager.getInstance().getElContextListeners())
        {
            listener.contextCreated(event);
        }

        return elContext;
    	
    }
    
	public Object evaluateExpression(String expression){
		ExpressionFactory expFactory = this.getExpressionFactory();
		
		ValueExpression valueExp = expFactory.createValueExpression(this.getELContext(), expression, Object.class);
		
		return valueExp.getValue(this.getELContext());
	}
	
	public String evaluateStringExp(String expression){
		
		Object valueObj = evaluateExpression(expression);
		return valueObj == null? null : valueObj.toString();
		
	}
	
	public void setValueToExpression(String expression, Object value){
		ExpressionFactory expFactory = this.getExpressionFactory();
		
		ValueExpression valueExp = expFactory.createValueExpression(this.getELContext(), expression, Object.class);
		
		valueExp.setValue(this.getELContext(), value);
	}
	
	public void sendELString(String expression){
		if(this.getWindow() == null){
			log.warning("The target window is null, sending string is canelled: " + expression);
		}
		
		String value = evaluateStringExp(expression);
		
		if(value != null){
			getWindow().getMessageSender().send(value);
		}
		
		
	}

	public void sendPlainString(String text){
		if(this.getWindow() == null){
			log.warning("The target window is null, sending string is canelled: " + text);
		}
		
		getWindow().getMessageSender().send(text);
	}
	
	
	public static void addELResolver(ELResolver elResolver){
		externalELResolverList.add(elResolver);
	}
    
    private static ELResolver buildELResolver(){
    	CompositeELResolver compositeResolver = new CompositeELResolver();
        
    
        // add the ELResolvers to a List first to be able to sort them
        //List<ELResolver> list = new ArrayList<ELResolver>();
        
    	compositeResolver.add(ImplicitObjectResolver.makeResolverForChatpages());
        
    	for(ELResolver resolver : externalELResolverList){
    		compositeResolver.add(resolver);
    	}
//    	list.addAll(externalELResolverList);
//        list.add(new CompositeComponentELResolver());

//        addFromRuntimeConfig(list);

//        list.add(new ManagedBeanResolver());
    	compositeResolver.add(new ResourceBundleELResolver());
    	compositeResolver.add(new MapELResolver());
    	compositeResolver.add(new ListELResolver());
    	compositeResolver.add(new ArrayELResolver());
    	compositeResolver.add(new BeanELResolver());
        
        // give the user a chance to sort the resolvers
//        sortELResolvers(list, Scope.Faces);
//        
//        // give the user a chance to filter the resolvers
//        Iterable<ELResolver> filteredELResolvers = filterELResolvers(list, Scope.Faces);
//        
//        // add the resolvers from the list to the CompositeELResolver
//        for (ELResolver resolver : filteredELResolvers)
//        {
//            compositeElResolver.add(resolver);
//        }
        
        // the ScopedAttributeResolver has to be the last one in every
        // case, because it always sets propertyResolved to true (per the spec)
    	compositeResolver.add(new ScopedAttributeResolver());
        
        
        
        return compositeResolver;
    }
    
    public ExpressionFactory getExpressionFactory(){
    	
    	return expressionFactory;
    	
    }
    
    public void release()
    {
        released = true;
        currentInstance.remove();
    }
    
    public boolean isReleased()
    {
        return released;
    }

	@Override
	public Iterator<String> iterator() {
		return attributeMap.keySet().iterator();
	}    


}
