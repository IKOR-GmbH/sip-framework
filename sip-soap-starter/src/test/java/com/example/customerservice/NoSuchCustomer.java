package com.example.customerservice;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Java class for NoSuchCustomer complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="NoSuchCustomer"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="customerName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "NoSuchCustomer",
    propOrder = {"customerName"})
public class NoSuchCustomer {

  @XmlElement(required = true, nillable = true)
  protected String customerName;

  /**
   * Gets the value of the customerName property.
   *
   * @return possible object is {@link String }
   */
  public String getCustomerName() {
    return customerName;
  }

  /**
   * Sets the value of the customerName property.
   *
   * @param value allowed object is {@link String }
   */
  public void setCustomerName(String value) {
    this.customerName = value;
  }
}
