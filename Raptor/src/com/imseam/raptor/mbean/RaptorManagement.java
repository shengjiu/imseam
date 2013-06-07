package com.imseam.raptor.mbean;

import javax.management.MXBean;

@MXBean
public interface RaptorManagement {
	
	void restart();
	
	void stop();

}
