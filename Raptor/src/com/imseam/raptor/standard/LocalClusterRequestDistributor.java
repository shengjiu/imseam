package com.imseam.raptor.standard;

import java.util.ArrayList;
import java.util.List;

import com.imseam.chatlet.IConnection;
import com.imseam.chatlet.IContext;
import com.imseam.chatlet.IEventErrorCallback;
import com.imseam.chatlet.IIdentiable.UidType;
import com.imseam.chatlet.IWindow;
import com.imseam.chatlet.exception.IdentifierNotExistingException;
import com.imseam.raptor.IChatletApplication;
import com.imseam.raptor.IMessengerConnection;
import com.imseam.raptor.IMessengerWindow;
import com.imseam.raptor.cluster.IClusterInvocation;
import com.imseam.raptor.cluster.IClusterInvocationDistributor;
import com.imseam.raptor.threading.PrioritizedTask;
import com.imseam.raptor.threading.RaptorTaskQueue;

public class LocalClusterRequestDistributor implements IClusterInvocationDistributor{

	private IChatletApplication application = null;
	
	@Override
	public void initApplication(IChatletApplication application) {
		this.application = application;
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public void distributeRequest(IEventErrorCallback handler,
			IClusterInvocation<? extends IContext> request, UidType idType, String targetUid)
			throws IdentifierNotExistingException {
		if(idType.equals(UidType.WINDOW)){
			distributeWindowRequest(handler, (IClusterInvocation<IWindow>)request, targetUid);
			return;
		}
		if(idType.equals(UidType.CONNECTION)){
			distributeConnectionRequest(handler, (IClusterInvocation<IConnection>)request, targetUid);
			return;
		}
		assert(false);
		
	}

	@Override
	public void distributeConnectionRequest(IEventErrorCallback handler,
			IClusterInvocation<IConnection> request, String... connectionUids)
			throws IdentifierNotExistingException {
		List<RequestTask<?>> taskList = new ArrayList<RequestTask<?>>();
		for(String uid : connectionUids){
			IMessengerConnection messengerConnection = application.getConnection(uid);
			if(messengerConnection == null){
				throw new IdentifierNotExistingException(UidType.CONNECTION, uid);
			}
			IConnection connection = messengerConnection.getConnectionContext();
			taskList.add(new RequestTask<IConnection>(uid, application, handler,request, connection));
		}
		
		for(RequestTask<?> task : taskList){
			RaptorTaskQueue.getInstance(task.getTargetUid()).addTask(task);
		}
	}

	@Override
	public void distributeWindowRequest(IEventErrorCallback handler,
			IClusterInvocation<IWindow> request, String... windowUids)
			throws IdentifierNotExistingException {
		List<RequestTask<?>> taskList = new ArrayList<RequestTask<?>>();
		if(request.toString().contains("To all windows In Meeting") && windowUids.length < 2){
			//System.out.println("LocalClusterRequestDistributor.distributeWindowRequest, targets size 1, and target[0]:" + windowUids[0]);
		}

		for(String uid : windowUids){
			IMessengerWindow messengerWindow = application.getWindowManager().getWindowByUid(uid);
			if(messengerWindow == null){
				throw new IdentifierNotExistingException(UidType.WINDOW, uid);
			}
			IWindow window = messengerWindow.getWindowContext();
			if(request.toString().contains("createSendMessageFunction")){
				//System.out.println("LocalClusterRequestDistributor.distributeWindowRequest, target:" + uid + ", " + request);
			}
			taskList.add(new RequestTask<IWindow>(uid, application, handler,request, window));
		}
		
		for(RequestTask<?> task : taskList){
			RaptorTaskQueue.getInstance(task.getTargetUid()).addTask(task);
		}
	}
	
	class RequestTask<T extends IContext> implements PrioritizedTask <Object, Long>{
		private IChatletApplication application;
		private IEventErrorCallback handler;
		private IClusterInvocation<T> request;
		private T context;
		private String targetUid;
		
		
		RequestTask(String targetUid, IChatletApplication application, IEventErrorCallback handler,
				IClusterInvocation<T> request, T context){
			assert(application != null);
			assert(request != null);
			this.targetUid = targetUid;
			this.application = application;
			this.handler = handler;
			this.request = request;
			this.context = context;
		}

		public String getTargetUid() {
			return targetUid;
		}

		public Long getPriority() {
			return Long.valueOf(request.getTimestamp().getTime());
		}

		public Object call() throws Exception {
			try{
				request.invoke(application, context, handler);
				if(context instanceof IWindow){
					IWindow windowContext = (IWindow)context;
					windowContext.getMessageSender().flush();
				}
			}catch(Exception exp){
				exp.printStackTrace();
				throw exp;
			}catch(Error error){
				error.printStackTrace();
			}
			return null;
		}
	}


	

}
