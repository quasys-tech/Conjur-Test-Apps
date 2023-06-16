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
```
