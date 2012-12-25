package com.imseam.common.util;

import com.imseam.common.util.MD5;

public class PasswordGenerator {
	private String strPwd = "1234567892";//password
	public String strPwd1="123456";
	public PasswordGenerator() {
	}
	/**
	 * 产生一个十位随机密码.
	 * @return String
	 */
	public String generateRandomPassword() {
		double randomSum = (Math.random() + 1) * 1000000000;
		Double rs = new Double(randomSum);
		long lRandomSum = rs.longValue();
		// System.out.println("the randomsum is "+ lRandomSum);
		String strRandomSum = String.valueOf(lRandomSum);
		String strPwd1 = strRandomSum.substring(0, 10);
		// System.out.println("the strPwd1 is" + strPwd1);
		this.strPwd = strPwd1;
		return strPwd1;
	}
	/**
	 * 用MD5对生成的十位随机密码进行加密
	 * @return String
	 */
	public String generateMD5() {
		MD5 md = new MD5();
		String m = md.getMD5ofStr(strPwd);
		return m;
	}
	
	public String ForgetPassword(String Password) {
		MD5 md = new MD5();
		String m = md.getMD5ofStr(Password);
		return m;
	}

	public static void main(String args[]) {
		//usage example as below:
		PasswordGenerator pg = new PasswordGenerator();
		String strPwd = pg.generateRandomPassword();
		String strMD5 = pg.generateMD5();
		System.out.println(strPwd);
		System.out.println(strMD5);
	}
}
