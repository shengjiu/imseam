package com.imseam.chatpage.config.util;

import java.io.File;
import java.io.StringWriter;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import com.imseam.chatpage.config.ChatPagesInfo;
import com.imseam.common.util.ExceptionUtil;


public class ChatPageConfigReader {
	
//	private static Log log = LogFactory.getLog(ChatPageConfigReader.class);
	
	public static void loadChatPagesFromConfigFile(String configFile, String parentPath){
		ConfigBasedObjectFactory.loadChatPages(parserChatPagesConfigFile(configFile), parentPath);
	}
	
	public static void loadChatPagesFromConfigResource(String resourceName, String parentPath){
		ConfigBasedObjectFactory.loadChatPages(parserChatPagesConfigResource(resourceName), parentPath);
	}
	
	public static ChatPagesInfo parserChatPagesConfigResource(String resource){
		try{
			return (ChatPagesInfo)getUnmarshaller().unmarshal(Thread.currentThread().getContextClassLoader().getResourceAsStream(resource));
		}catch(Exception exp){
			ExceptionUtil.wrapRuntimeException("Failed to parse the chat pages: " + resource, exp);
		}
		return null;
	}
	
	
	public static ChatPagesInfo parserChatPagesConfigFile(String configFile) {
		try {
			return (ChatPagesInfo)getUnmarshaller().unmarshal(new File(configFile));
		} catch (Exception exp) {
			ExceptionUtil.wrapRuntimeException("Failed to parse the chat pages: " + configFile, exp);
		}
		return null;
	}

	private static Unmarshaller getUnmarshaller(){
		try {
			// create a JAXBContext capable of handling classes generated into
			// the primer.po package
			JAXBContext jc = JAXBContext
					.newInstance("com.imseam.chatpage.config");

			// create an Unmarshaller
			Unmarshaller parser = jc.createUnmarshaller();

			SchemaFactory sf = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

			Schema schema = sf.newSchema(new StreamSource(ChatPagesInfo.class.getResourceAsStream("chat-page.xsd")));
			parser.setSchema(schema);
			
			
			return parser;

			// Marshaller m = jc.createMarshaller();
			// m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			// m.marshal(poe, System.out);
		} catch (Exception exp) {
			ExceptionUtil.wrapRuntimeException(
					"Creating chatpage config unmarshaller failed", exp);
		}
		return null;
	}	
	
	public static String marshallChatPagesConfig(ChatPagesInfo config) {
		try {
			// create a JAXBContext capable of handling classes generated into
			// the primer.po package
			JAXBContext jc = JAXBContext
					.newInstance("com.imseam.chatpage.config");
			StringWriter sw = new StringWriter();
			Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//			com.imseam.chatpage.config.ObjectFactory of = new com.imseam.chatpage.config.ObjectFactory();

			m.marshal(config, sw);
			return sw.toString();

		} catch (Exception exp) {
			ExceptionUtil.wrapRuntimeException(
					"Marshall chatpages Object failed.", exp);
		}

		return null;

	}

}
