package com.example.customerservice;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Java class for customerType.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <p>
 *
 * <pre>
 * &lt;simpleType name="customerType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="PRIVATE"/&gt;
 *     &lt;enumeration value="BUSINESS"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 */
@XmlType(name = "customerType")
@XmlEnum
public enum CustomerType {
  PRIVATE,
  BUSINESS;

  public String value() {
    return name();
  }

  public static CustomerType fromValue(String v) {
    return valueOf(v);
  }
}
