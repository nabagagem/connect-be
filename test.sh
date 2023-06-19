curl -X 'POST' \
  'http://localhost:8080/api/v1/jobs' \
  -H 'accept: */*' \
  -H 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJ1eVg5U0o4eVNJTjlzZVBFSnlKY0ttWlZIMlpFYXg1X1EtQXgzYUpvUGFnIn0.eyJleHAiOjE2ODY5NDM5NzAsImlhdCI6MTY4NjkwNzk3MCwiYXV0aF90aW1lIjoxNjg2OTA3OTcwLCJqdGkiOiI4Nzk5ZDdkNi03MDg5LTQwNGEtYWI1MS0zOWE2YmVhMWM3MGMiLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjI4MDgwL2F1dGgvcmVhbG1zL21hc3RlciIsInN1YiI6ImM2Mjk3NzNlLTVmODUtNDczMS1iZGZjLWEyNmYzOWYxYWRkOCIsInR5cCI6IkJlYXJlciIsImF6cCI6ImFkbWluLWNsaSIsInNlc3Npb25fc3RhdGUiOiI3OWM1OWVkYy1lMGJmLTQ1ZjEtOGUyMy02YjdlMTBmNjZjMzgiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIioiXSwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiNzljNTllZGMtZTBiZi00NWYxLThlMjMtNmI3ZTEwZjY2YzM4IiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJhZG1pbiJ9.OH4c_4tiDb1rbRF_IK8Mz2d3X7Cc02Id9Mcl9IiEKkotJmMQFFti0L2wH_cN1voHJQLtB6ech3mqHmvSkDOunxeJ6pnodcxb77eA_rQ0XnJ9i3wkk_NJqr6qBtVs4_dSN1aLoSCkB_OaMHJk5BJYoeWnBS-JrDLOt_e60RK8MiiTAGhBnYJuLerVYXSC9PVIusu5PzyoVknTAkdG_CPTG6VwUPGu5PQDjP-N0yyDi1g5oOFp_SF5VliQe3qxcR_0wxDGmgb0Ix7LzhJ4DGXrkjkzCdCsDLi94yi_6epPlCnosU-NCccph7kdR2KJve9QD96zIw_NNUsDh8BmIUpPag' \
  -H 'Content-Type: application/json' \
  -H 'Accept-Language: pt' \
  -d '{
  "title": "string",
  "budget": {
    "amount": 0,
    "currency": "EUR"
  },
  "jobCategory": "IT",
  "description": "stringstri",
  "jobSize": "S",
  "jobFrequency": "ONE_SHOT",
  "background": "string",
  "jobMode": "PRESENCE",
  "requiredAvailability": "SOON",
  "requiredDates": {
    "startAt": "2023-06-16T09:33:10.957Z",
    "finishAt": "2023-06-16T09:33:10.957Z"
  },
  "address": "string",
  "addressReference": "string",
  "requiredSkills": [
    "string"
  ],
  "jobStatus": "DRAFT",
  "tags": [
    "string"
  ]
}'