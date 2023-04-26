#!/bin/sh
curl -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: PUT" \
  -H "Access-Control-Request-Headers: X-Requested-With" \
  -X OPTIONS --verbose \
  http://localhost:8080/api/v1/profile/3ebe5926-e7c4-42d1-bf96-ca893c7b4a44/skills