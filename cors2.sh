#!/bin/sh
curl -H "Origin: http://example.com" \
  -H "Access-Control-Request-Method: GET" \
  -H "Access-Control-Request-Headers: X-Requested-With" \
  -X OPTIONS --verbose \
  https://api.ramifica.eu/api/v1/profile/3ebe5926-e7c4-42d1-bf96-ca893c7b4a44/skills