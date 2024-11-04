# Example Project for Security in Spring Boot and Microservices [![Twitter](https://img.shields.io/twitter/follow/piotr_minkowski.svg?style=social&logo=twitter&label=Follow%20Me)](https://twitter.com/piotr_minkowski)

[![CircleCI](https://circleci.com/gh/piomin/sample-spring-security-microservices.svg?style=svg)](https://circleci.com/gh/piomin/sample-spring-security-microservices)

[![SonarCloud](https://sonarcloud.io/images/project_badges/sonarcloud-black.svg)](https://sonarcloud.io/dashboard?id=piomin_sample-spring-security-microservices)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=piomin_sample-spring-security-microservices&metric=bugs)](https://sonarcloud.io/dashboard?id=piomin_sample-spring-security-microservices)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=piomin_sample-spring-security-microservices&metric=coverage)](https://sonarcloud.io/dashboard?id=piomin_sample-spring-security-microservices)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=piomin_sample-spring-security-microservices&metric=ncloc)](https://sonarcloud.io/dashboard?id=piomin_sample-spring-security-microservices)

In this project I'm demonstrating you the most interesting features of [Spring Cloud Project](https://spring.io/projects/spring-cloud) for building microservice-based architecture.

-----

I'm publishing on my blog and maintaining example repositories just as a hobby. But if you feel it's worth donating:

[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/piotrminkowski)

1. How to renew certificates in your Spring Boot apps on Kubernetes with **Cert Manager** and **Stakater Reloader**. The example is available in the branch [master](https://github.com/piomin/sample-spring-security-microservices/tree/master).  A detailed guide may be found in the following article: [Renew Certificates on Kubernetes with Cert Manager and Reloader](https://piotrminkowski.com/2022/12/02/renew-certificates-on-kubernetes-with-cert-manager-and-reloader/) 
2. How to reload `SslBundles` with Spring Boot and run the apps on Kubernetes. A detailed guide may be found in the following article: [Spring Boot SSL Hot Reload on Kubernetes](https://piotrminkowski.com/2024/02/19/spring-boot-ssl-hot-reload-on-kubernetes/)
3. How to use OAuth2 with Spring Cloud and integrate Spring Boot app with **Keycloak**. A detailed guide may be found in the following article: [Microservices with Spring Cloud Gateway, OAuth2 and Keycloak](https://piotrminkowski.com/2024/03/01/microservices-with-spring-cloud-gateway-oauth2-and-keycloak/)
4. How to use SAML2 with Spring Boot and integrate it with **Keycloak** through the OpenSAML **Shibboleth** library. A detailed guide may be found in the following article: [Spring Boot with SAML2 and Keycloak](https://piotrminkowski.com/2024/10/28/spring-boot-with-saml2-and-keycloak/)


## Getting Started

### SSL

To access an example with Spring Boot `SSLBundle` go to the `ssl` directory.
First, run the `secure-callme-bundle` app:
```shell
cd ssl/secure-callme-bundle
mvn spring-boot:run
```

First, run the `secure-caller-bundle` app:
```shell
cd ssl/secure-caller-bundle
mvn spring-boot:run
```

Then call the endpoint exposed by the with the curl command:
```shell
curl https://localhost:8444/caller/ping --insecure
```

### SAML2

To access an example with Spring Boot SAML 2.0 example go to the `saml` directory.
First, run the Keycloak container:
```shell
cd saml
docker compose up
```

Once the Keycloak is started go to `callme-saml` and run the app:
```shell
cd callme-saml
mvn spring-boot:run
```

### OAuth2

To access an example with Spring Boot OAuth2 example go to the `oauth` directory.
While building the `gateway` app it runs Testcontainer with Keycloak and simulates a downstream service:
```shell
cd oauth/gateway
mvn clean package
```
