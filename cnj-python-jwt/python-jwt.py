import os
import requests
import tempfile

def get_password(secretPath):
    conjur_baseurl = os.environ["CONJUR_APPLIANCE_URL"]
    conjur_account = os.environ["CONJUR_ACCOUNT"]
    conjur_serviceid = os.environ["CONJUR_AUTHN_JWT_SERVICE_ID"]
    cert_content = os.environ["CONJUR_SSL_CERTIFICATE"]

    with tempfile.NamedTemporaryFile(delete=False, suffix=".pem") as cert_file:
        cert_file.write(cert_content.encode('utf-8'))
        cert_file_path = cert_file.name

    try:

        with open('/var/run/secrets/kubernetes.io/serviceaccount/token', 'r') as file:
            conjur_jwttoken = file.read().rstrip()

        conjur_authn_header = {'Content-Type': 'application/x-www-form-urlencoded','Accept-Encoding': 'base64'}
        authn_request = requests.post(conjur_baseurl + '/authn-jwt/' + conjur_serviceid +'/'+ conjur_account +'/authenticate',data='jwt='+ conjur_jwttoken, headers=conjur_authn_header, verify=cert_file_path)
        print(authn_request.text)

        conjur_access_token = authn_request.text
        conjur_secret_header = {
            'Authorization': f'Token token="{conjur_access_token}"'
        }
        conjur_secret_get =  requests.get(conjur_baseurl + '/api/secrets/'+ conjur_account +'/variable/' + secretPath , headers=conjur_secret_header, verify=cert_file_path)
        print(conjur_secret_get.text)
        return conjur_secret_get.text
    finally:
        if os.path.exists(cert_file_path):
            os.remove(cert_file_path)

conjur_secretid = os.environ["CONJUR_SECRET_ID"]
password = get_password(conjur_secretid)

