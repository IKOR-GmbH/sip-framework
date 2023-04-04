package com.example.customerservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Java class for updateCustomer complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="updateCustomer"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="customer" type="{http://customerservice.example.com/}customer" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "updateCustomer",
    propOrder = {"customer"})
public class UpdateCustomer {

  protected Customer customer;

  /**
   * Gets the value of the customer property.
   *
   * @return possible object is {@link Customer }
   */
  public Customer getCustomer() {
    return customer;
  }

  /**
   * Sets the value of the customer property.
   *
   * @param value allowed object is {@link Customer }
   */
  public void setCustomer(Customer value) {
    this.customer = value;
  }

  @Override
  public String toString() {
    return "UpdateCustomer{" + "customer=" + customer + '}';
  }
}
