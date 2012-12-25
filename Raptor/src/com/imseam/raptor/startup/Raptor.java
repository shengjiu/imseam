/*
 * Copyright 1999,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.imseam.raptor.startup;


import java.io.FileInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.config.EngineConfig;
import com.imseam.chatlet.config.util.ConfigReader;
import com.imseam.common.util.ClassUtil;
import com.imseam.raptor.IChatletEngine;



/**
 * Startup/Shutdown shell program for Catalina.  The following command line
 * options are recognized:
 * <ul>
 * <li><b>-config {pathname}</b> - Set the pathname of the configuration file
 *     to be processed.  If a relative path is specified, it will be
 *     interpreted as relative to the directory pathname specified by the
 *     "catalina.base" system property.   [conf/server.xml]
 * <li><b>-help</b> - Display usage information.
 * <li><b>-stop</b> - Stop the currently running instance of Catalina.
 * </u>
 *
 * Should do the same thing as Embedded, but using a server.xml file.
 *
 * @author Craig R. McClanahan
 * @author Remy Maucherat
 * @version $Revision: 380229 $ $Date: 2006-02-23 15:28:29 -0600 (Thu, 23 Feb 2006) $
 */

public class Raptor {

    private static Log log = LogFactory.getLog( Raptor.class );
    // ----------------------------------------------------- Instance Variables


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


    // ----------------------------------------------------------- Main Program

    /**
     * The application main program.
     *
     * @param args Command line arguments
     */
    public static void main(String args[]) {
        (new Raptor()).process(args);
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
    
    public void stopServer() {
        stop();
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
            ("usage: java org.apache.catalina.startup.Catalina"
             + " [ -config {pathname} ]"
             + " [ -nonaming ] { start | stop }");

    }


 
    
    


}



