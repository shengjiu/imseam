package com.imseam.raptor.threading.test;

import org.testng.annotations.Test;

import com.imseam.raptor.threading.ReflectedQueuedTask;
import com.imseam.raptor.threading.annotation.QueuedTask;

public class ReflectedTaskTest {
	
	@Test(groups = { "Threading" })
	public void testAddTask() {
		
		ReflectedTaskTest testObject = new ReflectedTaskTest();
		
		ReflectedQueuedTask task = new ReflectedQueuedTask(testObject, "method1", "str1", "paramer1");
		task.perform();
		
	}
	
	@Test(groups = { "Threading" })
	public void testAddTaskWithWrongName() {
		
		ReflectedTaskTest testObject = new ReflectedTaskTest();
		try{
			new ReflectedQueuedTask(testObject, "method2", "str1", "paramer1");
		}catch(Exception exp){
			return;
		}
		assert(false);
		
	}
	
	@Test(groups = { "Threading" })
	public void testAddTaskWithDifferntParameters() {
		
		ReflectedTaskTest testObject = new ReflectedTaskTest();
		try{
			new ReflectedQueuedTask(testObject, "method1", new Integer(1), "paramer1");
		}catch(Exception exp){
			return;
		}
		assert(false);
		
	}
	
	@Test(groups = { "Threading" })
	public void testAddTaskWithWrongParameterNumber() {
		
		ReflectedTaskTest testObject = new ReflectedTaskTest();
		try{
			 new ReflectedQueuedTask(testObject, "method1", "param1", "param2", "additional parameter");
		}catch(Exception exp){
			return;
		}
		assert(false);
		
	}
	
	public ReflectedTaskTest()
	{
		
	}

	public void method1(String str1, Object obj){
		System.out.println(str1);
		System.out.println(obj);
	}
}
