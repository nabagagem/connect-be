#!/bin/sh
curl -v \
  -d "client_id=admin-cli" \
  -d "username=admin" \
  -d "password=ramifica33!" \
  -d "grant_type=password" \
  "https://auth.ramifica.eu/auth/realms/master/protocol/openid-connect/token" | jq