## Demo for Spring Boot SSL on Kubernetes with CertManager

### Steps to run demo locally 

#### Run `secure-callme`

Go to the `secure-callme` directory:
```shell
cd secure-callme
```

Export keystore password and cert location path:
```shell
export PASSWORD=123456
export CERT_PATH=/Users/pminkows/Download/
```

Run the app in the `dev` mode:
```shell
mvn spring-boot:run
```

#### Run `secure-caller`

Go to the `secure-caller` directory:
```shell
cd secure-caller
```

Export keystore password and cert location path:
```shell
export PASSWORD=123456
export CERT_PATH=/Users/pminkows/Download/
export CLIENT_CERT_PATH=/Users/pminkows/Download/
export HOST=localhost
```

Run the app in the `dev` mode:
```shell
mvn spring-boot:run
```

### Steps to run demo on Kubernetes