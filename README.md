### petstore-reference-app

Mastercard Petstore API
- API version: 1.0.0
- App version: 1.0.0
  
This is a sample API to demonstrate an API that aligns to Mastercard's API Gold Standards. It covers all expected use cases. Please see [here](https://developer.mastercard.com/reference-service-ngw/documentation) to view our sample documentation.

For more information, please visit [https://developer.mastercard.com/support](https://developer.mastercard.com/support)

## Requirements

Building the API client library requires:
1. Java 17+
2. Maven (3.8.3+)

## Installation

To being please run

```shell
mvn clean install
```

This will generate all required files as defined in [petstore.yaml](src/main/resources/petstore.yaml). They should be built into the target folder.

## Getting Started

Set properties in [application.properties](src/main/resources/application.properties)

The authentication mode must be set using (mTLS or oAuth).
```
-Dspring.profiles.active=oauth
-Dspring.profiles.active=mtls
```
If using the API in oAuth mode the following are required in [application-oauth.properties](src/main/resources/application-oauth.properties)
```
mastercard.oauth.pkcs12KeyFile
mastercard.oauth.consumerKey
mastercard.oauth.keyAlias
mastercard.oauth.keyPassword
```
If using the API in mTLS mode the following are required in [application-mtls.properties](src/main/resources/application-mtls.properties)
```
mastercard.mtls.pfxKeyFile
mastercard.mtls.keyPassword
```
Encryption properties are required to be set. This API supports JWE encryption. This is in [application.properties](src/main/resources/application.properties)
```
mastercard.encryption.encryptionCert
mastercard.encryption.decryptionKeys
mastercard.encryption.decryptionKeyAlias
mastercard.encryption.decryptionKeyPassword
```

## Use Cases

To see how the average flow of each use case please see the [flow folder](src/test/java/com/mastercard/app/petstore/flow) and run a test. Note this tests will not
run if a `basePath` in [application.properties](src/main/resources/application.properties) is not set. These tests call
out to the service so they must be set.

The tests can be run using
```shell
mvn test -Dspring.profiles.active=oauth
```
or 
```shell
mvn test -Dspring.profiles.active=mtls
```

### Adoption Flow Test Case
This demonstrates the typical flow for an adoption. 
1) A new adoption is created 
2) The adoption is then retrieved using the adoption id from step 1 
3) Information is updated before removing it using the same id
4) Clean up. The adoption is removed from the system

### Employee Flow Test Case
This demonstrates the typical flow for managing an employee.
1) A new employee is added to the system
2) The employee's information is then searched for via SSN
3) Clean up. The employee is removed from the system

### Pet Flow Test Case
This demonstrates the typical flow for adding a new cat.
1) A new cat is added to the system
2) The cat's information is retrieved via it's id
3) That cat's status is updated to `RESERVED`
4) Clean up. The cat is removed from the system

## Author

API_Consultancy_and_Standards@mastercard.com
