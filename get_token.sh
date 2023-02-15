#!/bin/sh
curl \
  -d "client_id=admin-cli" \
  -d "username=gemini.richard@gmail.com" \
  -d "password=passwd" \
  -d "grant_type=password" \
  "http://alb-keycloak-1028309459.us-east-1.elb.amazonaws.com:8080/auth/realms/master/protocol/openid-connect/token"