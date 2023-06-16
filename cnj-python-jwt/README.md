## Config Map
```yml
kind: ConfigMap
apiVersion: v1
metadata:
  name: conjur-cm
data:
  CONJUR_ACCOUNT: quasys
  CONJUR_APPLIANCE_URL: https://conjur-follower.conjur.svc.cluster.local
  CONJUR_AUTHN_JWT_SERVICE_ID: quasys
  CONJUR_SECRET_ID: vault/poc/QUASYS-CONJUR-SAFE/mydbuser/password
  CONJUR_SSL_CERTIFICATE: |
    -----BEGIN CERTIFICATE-----
    MIIDwjCCAqqgAwIBAgIUCIeb2X6ozC0yhwcjLkEBCnqxR3EwDQYJKoZIhvcNAQEL
    BQAwQDEMMAoGA1UECgwDY2pyMRIwEAYDVQQLDAlDb25qdXIgQ0ExHDAaBgNVBAMM
    -----END CERTIFICATE-----
```
