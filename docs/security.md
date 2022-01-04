## SIP Security

[TOC]

### Introduction

**What is SIP Security?**
SIP Security is built on top of Spring Security and is trying to ease and streamline the usage of Security related aspects,
but also add some enterprise grade features which are, from our perspective, quite relevant and helpful.

**Why use it?**
Nowadays, security is one of the most important topics in every application.
Implementing code and configuration on top of Spring Security should help you jump-start your adapter development
(along with the archetype), not waste your time, and enable you to focus on your core tasks - resolving integration problems.

### Dependency

```xml
<dependency>
    <groupId>de.ikor.sip.foundation</groupId>
    <artifactId>sip-security</artifactId>
</dependency>
```

### Contents

Following description shows SIP Security functionalities and configuration.

#### SSL
SSL is by default turned off. It can be activated by setting a server side certificate or turning the client SSL explicitly 
on and optionally setting a client certificate. If no server certificate is set a client certificate is mandatory when turning on client SSL.

- Client (our application is consuming APIs using the provided certificate)
  ```yaml
  sip.security:
      ssl:
          client:
              enabled: true #true / false (default); if enabled but no other configs are made, server keystore is used as client certificate
              key-store: classpath:keystore.p12 # possible resource strings are classpath:, file:, http:, _none_
              key-store-password: password # we recommend to use env_vars or sealed secrets
              key-store-type: pkcs12 #possible options are pkcs12, jks, jceks
              key-alias: springboot #the alias of the key to be chosen from the container
              key-password: password # we recommend to use env_vars or sealed secrets
  ```
  To turn the usage of a specific client certificate off (default):
  ```yaml
  sip.security:
      ssl:
          client:
              enabled: false
  ```
  The truststore is handled with Java default:
    - set the cacerts in the runtime accordingly (preferable approach)
    - for local development you could add any certificate, IntermediateCA, RootCA or complete certificate chain to the 
      keystore as a trusted certificate (e.g. by importing it with tools like keytool), a possible command could be:
  `keytool -importcert -file certificate.cer -keystore keystore.jks -alias "Alias"`

- Server (our application is providing APIs using the provided certificate)
  ```yaml
  sip.security:
      ssl:
          server:
              key-store: classpath:keystore.p12 # possible resource strings are classpath:, file:, http:, _none_
              key-store-password: password # we recommend to use env_vars or sealed secrets
              key-store-type: pkcs12 #possible options are pkcs12, jks, jceks
              key-alias: springboot #the alias of the key to be chosen from the container
              key-password: password # we recommend to use env_vars or sealed secrets
              client-auth: want # Could be: need (client auth is mandatory), want (client auth is is wanted but not 
              # mandatory) and none (default)
  ```
  To turn the usage of the server certificate off:
  ```yaml
  sip.security:
      ssl:
          server:
              enabled: false
  ```

#### Authentication
Global authentication configuration:

```yaml
sip.security:
    authentication:
        disable-csrf: true
        ignored-endpoints: #a list of endpoints which are ignored by ALL authenticators based on Spring´s AntPathMatchers implementation
        - /actuator
        - /actuator/**
        - /favicon.ico
```

- <u>Extractors</u> (extract the relevant token from an http-requests)
    - BasicAuth: Extracts the BasicAuth credentials from the request
    - X509: Extracts the X509 certificate from the request (requires sip.security.ssl.server.client-auth set to need or want)
- <u>Providers</u> (triggers a Validator to validate a given authentication)
    - BasicAuth: Takes the extracted basic auth token and uses the given validator from the configuration to validate the token
    - X509: Takes the extracted X509 token and uses the given validator from the configuration to validate the token
- <u>Validators</u> (validates a given token)
    - BasicAuth
      ```yaml
      sip.security:
          authentication:
              auth-providers: #authentication functionality is enabled if valid providers are defined
                  - classname: de.ikor.sip.foundation.security.authentication.basic.SIPBasicAuthAuthenticationProvider
                    ignored-endpoints: #a list of endpoints which are ignored by this specific authenticator based on Spring´s AntPathMatchers implementation
                      - /actuator/health
                      - /actuator/env
                    validation:
                      classname: de.ikor.sip.foundation.security.authentication.basic.SIPBasicAuthFileValidator #FQCN of the validator to be used
                      file-path: classpath:basic-auth-users.json  # possible resource strings are classpath:, file:, http:, _none_
      ```
      Sample file `basic-auth-users.json`:
      ```json
      [
          {"username": "user1", "password": "pw1"},
          {"username": "anotherUser", "password": "anotherPassword"}
      ]
      ```
  - X509 Configuration:
    ```yaml
    sip.security:
        authentication:
            auth-providers: #authentication functionality is enabled if valid providers are defined
                - classname: de.ikor.sip.foundation.security.authentication.x509.SIPX509AuthenticationProvider
                  ignored-endpoints: #a list of endpoints which are ignored by this specific authenticator based on Spring´s AntPathMatchers implementation
                    - /favicon.ico
                    - /actuator/env
                  validation:
                    classname: de.ikor.sip.foundation.security.authentication.x509.SIPX509FileValidator #FQCN of the validator to be used
                    file-path: classpath:client-certs.acl  # possible resource strings are classpath:, file:, http:, _none_
    ```
    Sample file `client-certs.acl`:
    ```text
    CN=Full Name, EMAILADDRESS=name@domain.de, O=[*], C=DE
    CN=Full Name2, EMAILADDRESS=name2@domain.de, O=[*], C=DE
    ```

### Additional information

- *ACL* in the context of SIP Security and X509 is our implementation of an *Access Control List*.
  `SIPX509FileValidator` uses a given .acl file to grant access to respective users. This SIP specific feature is not related to [Spring Security´s ACL](https://docs.spring.io/spring-security/site/docs/3.0.x/reference/domain-acls.html) implementation.
  The high-level intention is to limit the access to the provided API. If this feature is used client certificates are needed.

- To set the password for configuration value `sip.security.ssl.server.key-store-password` by environment variable you can do the following in your (bash) shell
  (Spring will automatically find the correct variable and set the value):
  > `export SIP_SECURITY_SSL_SERVER_KEY_STORE_PASSWORD="password"`
