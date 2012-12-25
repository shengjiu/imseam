package com.imseam.common.util;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import javax.naming.InitialContext;
import com.imseam.common.util.ExceptionUtil;

public class SendMail2 {

	private static final String SMTP_HOST = "smtp.163.com";
	private static final String SENDER_NAME = "imseam";
	private static final String SMTP_AUTH_USER = "xfwebs";
	private static final String SMTP_AUTH_PWD = "passnet";
	private static final String SENDER_EMAIL_ADDRESS = "xfwebs@163.com";
	private static final String SENDER_MESSAGE="Hi Guy"+"\n"+"Your account is currently inactive. You cannot use it until you visit the following link:"+"\n";
	private static Object sysproperties;

	public void sendConfirmation() {
		StringBuffer message = new StringBuffer();
		message.append("用户名称：");
		message.append("密码：");

		sendMessage("zbl-123@sohu.com", "请查收邮件", message.toString());
	}

	public void sendMessage(String recipient, String subject, String message) {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.host", SMTP_HOST);
		props.put("mail.smtp.user", SMTP_AUTH_USER);
		props.put("mail.smtp.password", SMTP_AUTH_PWD);

		Session session = Session.getDefaultInstance(props, null);
		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(SENDER_EMAIL_ADDRESS, SENDER_NAME));
			msg.setRecipient(Message.RecipientType.TO, new InternetAddress(
					recipient));
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			msg.setText(SENDER_MESSAGE
					+message);
			Transport transport = session.getTransport("smtp");
			transport.connect((String) props.get("mail.smtp.host"), props
					.getProperty("mail.smtp.user"), props
					.getProperty("mail.smtp.password"));//2
			transport.sendMessage(msg, msg.getAllRecipients());

		} catch (Exception e) {
			System.out.println(e);
		}

	}
	
	// Call jboss mail servcie send mail
	public void SendMail(String  recipient,String subject,String message ) {
		
        try {
		InitialContext ctx = new InitialContext() ;
		Session mailSession = (Session) ctx.lookup("java:Mail") ;
		Message msg = new MimeMessage(mailSession);
		InternetAddress toAddrs[] = new InternetAddress[1];
		toAddrs[0] = new InternetAddress(recipient);
		msg.setRecipients(Message.RecipientType.TO, toAddrs);
		msg.setFrom();
		msg.setSubject(subject);
		msg.setSentDate(new Date());
		String content = new String("Test");

		msg.setText(content);
        msg.setText(message);
		Transport.send(msg); 
		
		}
		   catch(Exception e)

	        {
			   ExceptionUtil.wrapRuntimeException(recipient,e);
			  
	            

	        }
	}

	public static void main(String[] args) {
		SendMail2 sendMail = new SendMail2();
		
		StringBuilder sb=new StringBuilder();
		String xp="user:"+"\n";
		xp+="Your account is currently inactive. You cannot use it until you visit the following link:";

		sendMail.sendMessage("xfwebs@163.com", "请查收注册信息!", "this is a test");

	}
}
