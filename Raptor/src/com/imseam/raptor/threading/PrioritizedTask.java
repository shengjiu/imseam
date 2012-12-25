package com.imseam.raptor.threading;

import java.util.concurrent.Callable;

public interface PrioritizedTask <V, T> extends Callable <V>{
	
	T getPriority();

}
