package com.example.customerservice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Java class for customer complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="customer"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="customerId" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="address" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="numOrders" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="revenue" type="{http://www.w3.org/2001/XMLSchema}double"/&gt;
 *         &lt;element name="test" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="birthDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="type" type="{http://customerservice.example.com/}customerType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "customer",
    propOrder = {
      "customerId",
      "name",
      "address",
      "numOrders",
      "revenue",
      "test",
      "birthDate",
      "type"
    })
public class Customer {

  protected int customerId;
  protected String name;

  @XmlElement(nillable = true)
  protected List<String> address;

  protected Integer numOrders;
  protected double revenue;
  protected BigDecimal test;

  @XmlSchemaType(name = "date")
  protected XMLGregorianCalendar birthDate;

  @XmlSchemaType(name = "string")
  protected CustomerType type;

  /** Gets the value of the customerId property. */
  public int getCustomerId() {
    return customerId;
  }

  /** Sets the value of the customerId property. */
  public void setCustomerId(int value) {
    this.customerId = value;
  }

  /**
   * Gets the value of the name property.
   *
   * @return possible object is {@link String }
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the value of the name property.
   *
   * @param value allowed object is {@link String }
   */
  public void setName(String value) {
    this.name = value;
  }

  /**
   * Gets the value of the address property.
   *
   * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
   * modification you make to the returned list will be present inside the JAXB object. This is why
   * there is not a <CODE>set</CODE> method for the address property.
   *
   * <p>For example, to add a new item, do as follows:
   *
   * <pre>
   *    getAddress().add(newItem);
   * </pre>
   *
   * <p>Objects of the following type(s) are allowed in the list {@link String }
   */
  public List<String> getAddress() {
    if (address == null) {
      address = new ArrayList<String>();
    }
    return this.address;
  }

  /**
   * Gets the value of the numOrders property.
   *
   * @return possible object is {@link Integer }
   */
  public Integer getNumOrders() {
    return numOrders;
  }

  /**
   * Sets the value of the numOrders property.
   *
   * @param value allowed object is {@link Integer }
   */
  public void setNumOrders(Integer value) {
    this.numOrders = value;
  }

  /** Gets the value of the revenue property. */
  public double getRevenue() {
    return revenue;
  }

  /** Sets the value of the revenue property. */
  public void setRevenue(double value) {
    this.revenue = value;
  }

  /**
   * Gets the value of the test property.
   *
   * @return possible object is {@link BigDecimal }
   */
  public BigDecimal getTest() {
    return test;
  }

  /**
   * Sets the value of the test property.
   *
   * @param value allowed object is {@link BigDecimal }
   */
  public void setTest(BigDecimal value) {
    this.test = value;
  }

  /**
   * Gets the value of the birthDate property.
   *
   * @return possible object is {@link XMLGregorianCalendar }
   */
  public XMLGregorianCalendar getBirthDate() {
    return birthDate;
  }

  /**
   * Sets the value of the birthDate property.
   *
   * @param value allowed object is {@link XMLGregorianCalendar }
   */
  public void setBirthDate(XMLGregorianCalendar value) {
    this.birthDate = value;
  }

  /**
   * Gets the value of the type property.
   *
   * @return possible object is {@link CustomerType }
   */
  public CustomerType getType() {
    return type;
  }

  /**
   * Sets the value of the type property.
   *
   * @param value allowed object is {@link CustomerType }
   */
  public void setType(CustomerType value) {
    this.type = value;
  }

  @Override
  public String toString() {
    return "Customer{"
        + "customerId="
        + customerId
        + ", name='"
        + name
        + '\''
        + ", address="
        + address
        + ", numOrders="
        + numOrders
        + ", revenue="
        + revenue
        + ", test="
        + test
        + ", birthDate="
        + birthDate
        + ", type="
        + type
        + '}';
  }
}
