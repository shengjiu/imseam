package com.imseam.test;

import java.io.Serializable;

public interface RemoteInvocation extends Serializable{
	Object invoke(Object parameter);
}
