package com.imseam.raptor;

import com.imseam.chatlet.config.EngineConfig;



public interface IChatletEngine {


    // ------------------------------------------------------------- Properties


    /**
     * Return descriptive information about this Server implementation and
     * the corresponding version number, in the format
     * <code>&lt;description&gt;/&lt;version&gt;</code>.
     */
    public String getInfo();


 

    // --------------------------------------------------------- Public Methods


 

    /**
     * Wait until a proper shutdown command is received, then return.
     */
    public void await();



 
    /**
     * Invoke a pre-startup initialization. This is used to allow connectors
     * to bind to restricted ports under Unix operating environments.
     *
     * @exception LifecycleException If this server was already initialized.
     */
    public void initialize(EngineConfig engineConfig);
    
    public void start();
    
    public void stop();
}
 
