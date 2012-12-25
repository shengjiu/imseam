package com.imseam.common.util.test;

import org.testng.annotations.Test;


import com.imseam.common.util.*;


public class PasswordGeneratorTest {

	@Test(groups = { "com.imseam.common.util" })
	public void test() {
		PasswordGenerator pg = new PasswordGenerator();
		String strPwd = pg.generateRandomPassword();
		String strMD5 = pg.generateMD5();
		assert(pg.ForgetPassword("123456")!=null);
		assert(strPwd != null);
		assert(strMD5 != null);
	}
	
	@Test(groups = { "com.imseam.common.util" })
	public void PublicFunctionTest(){
		PublicFunction pb=new PublicFunction();
		long longtest=2;
		String strMailAddress="xfwebs@tom.com";
		assert pb.StringTolong("2")==longtest;
		assert pb.validateEmailFormat(strMailAddress);
		
	}
	
	
}
