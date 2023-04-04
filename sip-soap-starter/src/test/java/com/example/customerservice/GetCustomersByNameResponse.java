package com.example.customerservice;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Java class for getCustomersByNameResponse complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="getCustomersByNameResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="return" type="{http://customerservice.example.com/}customer" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "getCustomersByNameResponse",
    propOrder = {"_return"})
public class GetCustomersByNameResponse {

  @XmlElement(name = "return")
  protected List<Customer> _return;

  /**
   * Gets the value of the return property.
   *
   * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
   * modification you make to the returned list will be present inside the JAXB object. This is why
   * there is not a <CODE>set</CODE> method for the return property.
   *
   * <p>For example, to add a new item, do as follows:
   *
   * <pre>
   *    getReturn().add(newItem);
   * </pre>
   *
   * <p>Objects of the following type(s) are allowed in the list {@link Customer }
   */
  public List<Customer> getReturn() {
    if (_return == null) {
      _return = new ArrayList<Customer>();
    }
    return this._return;
  }
}
