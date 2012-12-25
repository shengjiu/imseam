package com.imseam.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import com.imseam.common.util.ExceptionUtil;

public class SerializerUtil {
	
	public static byte[] serialize(Object object){
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		byte[] bytes = null;
		try{
			out = new ObjectOutputStream(bos);   
			out.writeObject(object);
			bytes = bos.toByteArray();
		} catch (IOException e) {
			ExceptionUtil.wrapRuntimeException(e);
		}finally{
			try {
				out.close();
				bos.close();
			} catch (IOException e) {
				ExceptionUtil.wrapRuntimeException(e);
			}
		}
		return bytes;
	}
	
	public static Object deserialize(byte [] bytes){
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInput in = null;
		Object object = null; 

		try{
			in = new ObjectInputStream(bis);
			object = in.readObject(); 
		} catch (Exception e) {
			ExceptionUtil.wrapRuntimeException(e);
		} finally{
			try {
				bis.close();
				in.close();
			} catch (IOException e) {
				ExceptionUtil.wrapRuntimeException(e);
			}
		}
		return object;
	}

}
