package com.imseam.chat.connector.jmsn;

import rath.msnm.Debug;
import rath.msnm.MSNMessenger;
import rath.msnm.UserStatus;

public class Client {

	public static void main(String[] args) throws Exception{
		String Email = "imseam.jmsn1@hotmail.com";
		String Password = "Password1";
		// create a new MSNMessenger instance msn
		MSNMessenger msn = new MSNMessenger(Email, Password);
		
		// set the initial status to online.
		msn.setInitialStatus(UserStatus.ONLINE);
		
		Adapter adapter = new Adapter(msn);
		// register your pre-created adapter to msn
		msn.addMsnListener(adapter);
		
		Debug.printOutput = true;
		Debug.printInput = true;
		Debug.printMime = true;
		
		// login into the msn network
		msn.login();

		System.out.println("Waiting for the response....");
		

		
//		msn.doCall("wangshengjiu@hotmail.com");
		//msn.addFriendAsList()
		
		Thread.sleep(20000);
		
		msn.doCall("wangshengjiu@hotmail.com");
		
		Thread.sleep(30000);
		
		adapter.session.close();
		
		
		
	}

}
