//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.03.30 at 11:24:19 PM MDT 
//


package com.imseam.chatpage.config;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for foreach complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="foreach">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element name="text" type="{http://www.imseam.com/}text" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="foreach" type="{http://www.imseam.com/}foreach" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/choice>
 *       &lt;attribute name="render" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="var" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="items" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "foreach", propOrder = {
    "textOrForeach"
})
public class ForeachInfo {

    @XmlElements({
        @XmlElement(name = "foreach", type = ForeachInfo.class),
        @XmlElement(name = "text", type = TextInfo.class)
    })
    protected List<Object> textOrForeach;
    @XmlAttribute
    protected String render;
    @XmlAttribute(required = true)
    protected String var;
    @XmlAttribute(required = true)
    protected String items;

    /**
     * Gets the value of the textOrForeach property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the textOrForeach property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTextOrForeach().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Foreach }
     * {@link Text }
     * 
     * 
     */
    public List<Object> getTextOrForeach() {
        if (textOrForeach == null) {
            textOrForeach = new ArrayList<Object>();
        }
        return this.textOrForeach;
    }

    /**
     * Gets the value of the render property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRender() {
        return render;
    }

    /**
     * Sets the value of the render property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRender(String value) {
        this.render = value;
    }

    /**
     * Gets the value of the var property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVar() {
        return var;
    }

    /**
     * Sets the value of the var property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVar(String value) {
        this.var = value;
    }

    /**
     * Gets the value of the items property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getItems() {
        return items;
    }

    /**
     * Sets the value of the items property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setItems(String value) {
        this.items = value;
    }

}
