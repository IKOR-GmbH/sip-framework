# SIP Integration Starter

[TOC]

## Description

Starter project used to add necessary predefined dependencies for development of integration adapters. If the Starter Parent is used than this dependency is included transitively and there is no need to define it in the _pom.xml_.

## Includes

- IKOR SIP Framework dependencies
- Camel dependencies
- Spring dependencies

## Usage

Add sip-integration-starter dependency to project.

```xml
<dependency>
    <groupId>de.ikor.sip.foundation</groupId>
    <artifactId>sip-integration-starter</artifactId>
    <version>${sip.integration.starter}</version>
</dependency>
```
