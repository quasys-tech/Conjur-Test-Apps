



def get_secret(secretPath):
    conjur_baseurl = env["CONJUR_APPLIANCE_URL"]
    conjur_account = env["CONJUR_ACCOUNT"]
    conjur_serviceid = env["CONJUR_AUTHN_JWT_SERVICE_ID"]
    
    with open('/var/run/secrets/kubernetes.io/serviceaccount/token', 'r') as file:
        conjur_jwttoken = file.read().rstrip()
        
    conjur_authn_header = {'Content-Type':'application/x-www-form-urlencoded','Accept-Encoding': 'base64'}
    authn_request = requests.post(conjur_baseurl + '/authn-jwt/' + conjur_serviceid +'/'+ conjur_account +'/authenticate',data='jwt='+ conjur_jwttoken, headers=conjur_authn_header, verify=False)
    print(authn_request.text)
    
    conjur_access_token = authn_request.text
    conjur_secret_header = "Authorization", "Token token=\"" + conjur_access_token + "\""
    conjur_secret_get =  requests.get(conjur_baseurl + '/api/secrets/'+ conjur_account +'/variable/' + secretPath , headers=conjur_secret_header, verify=False)
    print(conjur_secret_get.text)
    return conjur_secret_get.text



conjur_secretid = env["CONJUR_SECRET_ID"]
password = get_secret(conjur_secretid)