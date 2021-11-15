# IKOR SIP Middle Component

**\[List of Content\]**

- [Description](#description)
- [How to use?](#how-to-use)
- [Examples](#examples)

## Description

SIP middle component relates to the key SIP concept - splitting integration logic into submodules (connectors).
Combining connectors into one data flow is quite easy with Camel and can be done in many ways. However, if we
do it with one of Camel's existing component, we couldn't be sure if it represents connection between connectors or just
some intermediate routing within the single connector. If we (properly) use sipmc component,
it provides a clear functional and visual distinction between a connector's ending or beginning.
With it, adapter routes become more standardized and manageable.
Middle component is also a point of higher control over Apache Camel flow, as it provides a placeholder for future customizations
which will make it easier to bridge connectors in different ways, e.g. by using JMS broker implicitly if configured.

Middle component hides connecting technology and makes connection of two sub-systems abstract and simple. Current version
supports only in-memory connection channel implemented on top of Camel's SEDA component. It provides 1 to 1 and
publish-subscribe patterns and can support many channels in single adapter. Middle component can automatically determine
the number of consumers for a specific channel and auto-configure itself accordingly, as a 1 to 1 or publish-subscribe connector.

## How to use?

Middle component is used like any other Camel component, but has a simple URI pattern:

```
sipmc:channelName
```

The channel name is an arbitrary String value, but it should relate to one integration topic, e.q. partner.
Multiple sipmc routes may exist with different channel names.

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
