package com.imseam.raptor.xmbean;

public class RaptorService {
// public class RaptorService extends ServiceMBeanSupport implements
//		RaptorServiceMBean{
//	
//	private static Log log = LogFactory.getLog(RaptorService.class);
//	
//	private Raptor raptorServer = new Raptor();
//
//	public String getName() {
//		log.info("RaptorService ============ getName()");
//		return "Raptor:service=RaptorService";
//	}
//
//	public int getState() {
//		log.info("RaptorService ============ getState()");		
//		return 0;
//	}
//
//	public String getStateString() {
//		log.info("RaptorService ============ getStateString()");		
//		return null;
//	}
//
//	public void jbossInternalLifecycle(String arg0) throws Exception {
//		log.info("RaptorService ============ jbossInternalLifecycle()");
//	}
//
//	public void create() throws Exception {
//		log.info("RaptorService ============ create()");
//		raptorServer.process(new String[]{"start", "-config",  ImseamRaptorConfig.instance().getEngineConfigLocation()});
//	}
//
//	public void start() throws Exception {
//		log.info("RaptorService ============ start()");
//		create();
//	}
//
//	public void stop() {
//		log.info("RaptorService ============ stop()");
//		raptorServer.process(new String[]{"stop"});
//	}
//
//	public void destroy() {
//		log.info("RaptorService ============ destroy()");
//	}
//
//	public ObjectName preRegister(MBeanServer server, ObjectName name)
//			throws Exception {
//		log.info("RaptorService ============ preRegister()");
//		return null;
//	}
//
//	public void postRegister(Boolean registrationDone) {
//		log.info("RaptorService ============ postRegister()");
//	}
//
//	public void preDeregister() throws Exception {
//		log.info("RaptorService ============ preDeregister()");
//	}
//
//	public void postDeregister() {
//		log.info("RaptorService ============ postDeregister()");
//	}

}
