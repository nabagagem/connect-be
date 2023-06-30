#!/bin/sh
curl -v -d'client_id=admin-cli' -d 'username=admin' -d 'password=passwd' -d 'grant_type=password' \
    'http://localhost:28080/auth/realms/master/protocol/openid-connect/token'