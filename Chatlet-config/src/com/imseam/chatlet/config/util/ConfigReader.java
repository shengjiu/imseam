package com.imseam.chatlet.config.util;

import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import com.imseam.chatlet.config.EngineConfig;
import com.imseam.common.util.ExceptionUtil;


public class ConfigReader {

	public static EngineConfig parserEngineConfig(InputStream configInputStream) {
		try {
			// create a JAXBContext capable of handling classes generated into
			// the primer.po package
			JAXBContext jc = JAXBContext
					.newInstance("com.imseam.chatlet.config");

			// create an Unmarshaller
			Unmarshaller parser = jc.createUnmarshaller();

			SchemaFactory sf = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

			Schema schema = sf.newSchema(new StreamSource(ConfigReader.class.getResourceAsStream("/com/imseam/chatlet/config/chatlet-config.xsd")));
			parser.setSchema(schema);
			
			EngineConfig config = ((JAXBElement<EngineConfig>)parser.unmarshal(configInputStream)).getValue();


			return config;
			// Marshaller m = jc.createMarshaller();
			// m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			// m.marshal(poe, System.out);

		} catch (Exception exp) {
			ExceptionUtil.wrapRuntimeException(
					"Parse Raptor Engine Config file failed.", exp);
		}

		return null;

	}

	public static String marshallEngineConfig(EngineConfig config) {
		try {
			// create a JAXBContext capable of handling classes generated into
			// the primer.po package
			JAXBContext jc = JAXBContext
					.newInstance("com.imseam.chatlet.config");
			StringWriter sw = new StringWriter();
			Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			com.imseam.chatlet.config.ObjectFactory of = new com.imseam.chatlet.config.ObjectFactory();
			m.marshal(of.createChatletEngine(config), sw);
			return sw.toString();

		} catch (Exception exp) {
			ExceptionUtil.wrapRuntimeException(
					"Marshall EngineConfig Object failed.", exp);
		}

		return null;

	}
	

}
