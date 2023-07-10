# SIP Middle Component

[TOC]

## Description

SIP middle component is a tool used by the SIP framework to link connectors inside adapters. 
It's built as a custom Apache Camel component, used inside integration scenarios, 
but it's also available to adapter developers. 
The goal was to represent where one integration side ends and the other begins
through clear functional and visual distinction (via graphical interface).

Middle component hides connecting technology and makes connection of two sub-systems abstract and simple. 
It supports only in-memory connection channel implemented on top of Camel's SEDA component. It provides 1 to 1 and
publish-subscribe patterns and can support many channels in single adapter. Middle component can automatically determine
the number of consumers for a specific channel and autoconfigure itself accordingly, as a 1 to 1 or publish-subscribe connector.
In any case it will be autoconfigured with waitForTaskToComplete=always, meaning that it will wait for all asynchronous   
processors to finish before it continues route execution. 

## How to use

Middle component is used like any other Camel component, but has a simple URI pattern:

```
sipmc:channelName
```

The channel name is an arbitrary String value, but it should relate to one integration topic, e.q. partner.
Multiple sipmc routes may exist with different channel names.

## Middle Component with Declarative Structure

When building adapter by using Declarative Structure sipmc is embedded and explicit usage is not necessary. 
Sipmc is placed in Integration Scenario, and acts as a Camel route connection between Inbound and Outbound Connectors.
This means all inbound connectors will end with sipmc, while outbound connectors will start with sipmc.

## Examples

**1 to 1:**
In this scenario response provided by "mock:output" should be propagated back to the caller - "mock:input"

```java
 from("mock:input")
    .to("sipmc:foo");

 from("sipmc:foo")
    .to("mock:output");
```

**Publish subscribe:**
With publish-subscribe pattern the caller (mock-input) does not know how many consumers are registered. Middle component
makes sure the message is delivered to all of them. Notice that the URIs do not differ too much from the ones in
previous case.

```java
 from("mock:input")
    .to("sipmc:foo");

from("sipmc:foo")
    .to("mock:output1");
from("sipmc:foo")
    .to("mock:output2");
```

## Unit testing with sipmc

Middle Component, as any custom Camel component, should be mocked within unit tests which test and cover Camel routes.
Otherwise, running the test will run the route within Camel context without Middle component and test will fail running.

When doing Camel testing, if you use Camel's `CamelTestSupport` class, Middle component can be mocked by using 
AdwiceWith inside your test. Following code and methods within Advice can be used for mocking sipmc: 


```java
@Test
void test() throws Exception {
    // arrange
    AdviceWith.adviceWith(context.getRouteDefinitions().get(0), context, new AdviceWithRouteBuilder() {
        @Override
        public void configure() throws Exception {
            
            // replace sipmc consumer with mocking seda
            replaceFromWith("seda:mockConsumer");
            
            // replace sipmc producer with mocking seda
            weaveByToUri("sipmc:someChannel").replace().to("seda:mockProducer");
        }
    });
    
    context().start();
}
```

