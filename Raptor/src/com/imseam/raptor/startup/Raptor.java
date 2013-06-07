package com.imseam.raptor.startup;


import java.io.FileInputStream;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;

import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.config.EngineConfig;
import com.imseam.chatlet.config.util.ConfigReader;
import com.imseam.common.util.ClassUtil;
import com.imseam.raptor.IChatletEngine;
import com.imseam.raptor.mbean.RaptorManagement;



public class Raptor implements RaptorManagement{

    private static Log log = LogFactory.getLog( Raptor.class );


    /**
     * Pathname to the server configuration file.
     */
    protected String configFile = "conf/engine.xml";

    /**
     * Default engine class
     */
    protected String DEFAULT_ENGINE_CLASS = "com.imseam.raptor.standard.Engine";

    /**
     * The server component we are starting or stopping
     */
    protected IChatletEngine engine = null;


    /**
     * Are we starting a new server?
     */
    protected boolean starting = false;


    /**
     * Are we stopping an existing server?
     */
    protected boolean stopping = false;


    
    
    protected boolean await = false;

    private static Raptor raptor = null;

    // ----------------------------------------------------------- Main Program

    /**
     * The application main program.
     *
     * @param args Command line arguments
     */
    public static void main(String args[]) throws Exception{
    	 MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 

    	 ObjectName mxbeanName;
		
         mxbeanName = new ObjectName("com.imseam:type=Raptor");
 
         raptor = new Raptor();
         
         mbs.registerMBean(raptor, mxbeanName);    	
         
         
         raptor.process(args); 
         synchronized(raptor){
        	 raptor.wait();
         }
         System.out.println("Stopped!");
    }


    /**
     * The instance main program.
     *
     * @param args Command line arguments
     */
    public void process(String args[]) {

 
        try {
            if (arguments(args)) {
                if (starting) {
                    load(args);
                    start();
                } else if (stopping) {
                    stopServer();
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }
    
    public void restart(){
    	engine.stop();
    	load();
    	start();
    }		
    
    public void stopServer() {
    	System.out.println("Connect to JMX service.");
		JMXServiceURL url;
		try {
			url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://:9999/jmxrmi");
		
			JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
			MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
	

			ObjectName mbeanName = new ObjectName("com.imseam:type=Raptor");
			RaptorManagement mbeanProxy = JMX.newMBeanProxy(mbsc, mbeanName, RaptorManagement.class, true);
	
			System.out.println("Connected to: RaptorManagement bean");
	
			mbeanProxy.stop();
			jmxc.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    // ------------------------------------------------------ Protected Methods


    /**
     * Process the specified command line arguments, and return
     * <code>true</code> if we should continue processing; otherwise
     * return <code>false</code>.
     *
     * @param args Command line arguments to process
     */
    protected boolean arguments(String args[]) {

        if (args.length < 1) {
            usage();
            return (false);
        }

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-config")) {
            	i++; 
            	configFile = args[i];
            } else if (args[i].equals("-help")) {
                usage();
                return (false);
            } else if (args[i].equals("start")) {
                starting = true;
                stopping = false;
            } else if (args[i].equals("stop")) {
                starting = false;
                stopping = true;
            } else {
                usage();
                return (false);
            }
        }

        return (true);

    }



    /**
     * Start a new server instance.
     */
    public void load() {

        // Create and execute reading config
        long t1 = System.currentTimeMillis();
    	
    	//ChatletAppConfig appConfig = ConfigParser.parserAppConfig("");
        try{
    	
	    	EngineConfig engineConfig = ConfigReader.parserEngineConfig(new FileInputStream(configFile));
	        
	        String engineClassName = engineConfig.getClassName();
	        if((engineClassName == null) || ("").equalsIgnoreCase(engineClassName.trim())){
	        	engineClassName = DEFAULT_ENGINE_CLASS;
	        }
	        engine = (IChatletEngine) ClassUtil.createInstance(engineClassName); 
	
	        engine.initialize(engineConfig);
	 
	        long t2 = System.currentTimeMillis();
	        log.info("Initialization processed in " + (t2 - t1) + " ms");
        }
        catch(Exception exp){
        	log.error("Raptor initialization failed!", exp);
        }

    }


    /* 
     * Load using arguments
     */
    public void load(String args[]) {

//        try {
            if (arguments(args))
                load();
//        } catch (RuntimeException e) {
//           log.error(e);
//        }
    }

    public void create() {

    }

    public void destroy() {

    }

    /**
     * Start a new server instance.
     */
    public void start() {

        if (engine == null) {
            load();
        }

        long t1 = System.currentTimeMillis();

        // Start the new server
        engine.start();

        long t2 = System.currentTimeMillis();
        if(log.isInfoEnabled())
            log.info("Server startup in " + (t2 - t1) + " ms");

        
        if (await) {
            await();
            stop();
        }

    }


    /**
     * Stop an existing server instance.
     */
    public void stop() {
    	engine.stop();
    	synchronized(this){
    		this.notifyAll();
    	}
    }


    /**
     * Await and shutdown.
     */
    public void await() {

    	engine.await();

    }


    /**
     * Print usage information for this application.
     */
    protected void usage() {

        System.out.println
            ("usage: java com.imseam.raptor.startup.Raptor"
             + " [ -config {pathname} ]"
             + " [ -nonaming ] { start | stop }");

    }


 
    
    


}



