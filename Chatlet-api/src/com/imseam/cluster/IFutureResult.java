package com.imseam.cluster;

public interface IFutureResult<T> {
	
	T get() throws ResultIsNotReadyException;
	
	static public class ResultIsNotReadyException extends Exception {
		private static final long serialVersionUID = 7040247348699293142L;
	}
}
