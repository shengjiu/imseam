//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.08.31 at 10:52:09 PM MDT 
//


package com.imseam.chatlet.config;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for connection-factories complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="connection-factories">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="connection-factory" type="{http://www.imseam.com/}connection-factory-config" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "connection-factories", propOrder = {
    "connectionFactory"
})
public class ConnectionFactories {

    @XmlElement(name = "connection-factory")
    protected List<ConnectionFactoryConfig> connectionFactory;

    /**
     * Gets the value of the connectionFactory property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the connectionFactory property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getConnectionFactory().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ConnectionFactoryConfig }
     * 
     * 
     */
    public List<ConnectionFactoryConfig> getConnectionFactory() {
        if (connectionFactory == null) {
            connectionFactory = new ArrayList<ConnectionFactoryConfig>();
        }
        return this.connectionFactory;
    }

}
