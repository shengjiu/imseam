package com.imseam.raptor.cluster;

import com.imseam.cluster.IClusterCache;
import com.imseam.raptor.IChatletApplication;

public interface IRaptorClustercache extends IClusterCache{
	
	void init(IChatletApplication application);
	

}
