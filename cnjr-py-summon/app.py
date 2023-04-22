#!/bin/sh


import os
from http.server import HTTPServer, CGIHTTPRequestHandler
import socket
import sys

print("Checking credentials")

haveSecrets = True

secretKeys = ["APP_USERNAME", "APP_PASSWORD"]

for key in secretKeys:
    if key not in os.environ:
        print("%s not available" % key)
        sys.stdout.flush()
        haveSecrets = False

if not haveSecrets:
    sys.exit()

print("APP_USERNAME: ", os.environ['APP_USERNAME'])
print("APP_PASSWORD: ", os.environ['APP_PASSWORD'])
print("Success!")
sys.stdout.flush()
os.chdir('.')
server_object = HTTPServer(server_address=('', 8080), RequestHandlerClass=CGIHTTPRequestHandler)
server_object.serve_forever()