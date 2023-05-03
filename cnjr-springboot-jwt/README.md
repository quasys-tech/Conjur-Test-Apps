# HelloWorld conjur jwt
This project demonstrate to fetch secret from conjur using WebClient and HttpClient.

## To build project
```shell
./mvnw clean package
```

## To run project on local
```shell
./mvnw spring-boot:run
```

## Config Map
```yml
kind: ConfigMap
apiVersion: v1
metadata:
  name: conjur-cm
  namespace: {{ APP_Namespace}}
data:
  CONJUR_ACCOUNT: {{ Conjur_Account }}
  CONJUR_APPLIANCE_URL: https://conjur-follower.conjur.svc.cluster.local
  CONJUR_AUTHN_JWT_SERVICE_ID: {{ Service_ID }}
  CONJUR_SECRET_ID: {{ Secret Path }}
  CONJUR_SSL_CERTIFICATE: |
    -----BEGIN CERTIFICATE-----
    MIIDwjCCAqqgAwIBAgIUCIeb2X6ozC0yhwcjLkEBCnqxR3EwDQYJKoZIhvcNAQEL
    BQAwQDEMMAoGA1UECgwDY2pyMRIwEAYDVQQLDAlDb25qdXIgQ0ExHDAaBgNVBAMM
    -----END CERTIFICATE-----
```
