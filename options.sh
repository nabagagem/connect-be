#!/bin/sh
curl -v -X 'OPTIONS' \
  'https://api.ramifica.eu/api/v1/profile/3ebe5926-e7c4-42d1-bf96-ca893c7b4a44/skills' \
  -H 'accept: */*' \
  -H 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJwQ1FuczlDaWtkYk5XOUlVT3pwaTlTQVZEN2Q5V0s2Z3BIcktSbjJWTDFZIn0.eyJleHAiOjE2ODIzOTE4ODYsImlhdCI6MTY4MjM1NTg4OCwiYXV0aF90aW1lIjoxNjgyMzU1ODg2LCJqdGkiOiJlN2FmYjA1ZS0zN2UzLTQxNWItOWViYi0yMTIyNWExNzRhY2QiLCJpc3MiOiJodHRwczovL2F1dGgucmFtaWZpY2EuZXUvYXV0aC9yZWFsbXMvbWFzdGVyIiwic3ViIjoiM2ViZTU5MjYtZTdjNC00MmQxLWJmOTYtY2E4OTNjN2I0YTQ0IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYWRtaW4tY2xpIiwic2Vzc2lvbl9zdGF0ZSI6ImMyOGQ0Njg4LWUzYmEtNGEzNS1hMTM0LWZhMTRhZGE4NzU4OCIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJzY29wZSI6InByb2ZpbGUgZW1haWwiLCJzaWQiOiJjMjhkNDY4OC1lM2JhLTRhMzUtYTEzNC1mYTE0YWRhODc1ODgiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwibmFtZSI6IlJpY2FyZG8gQmF1bWFubiIsInByZWZlcnJlZF91c2VybmFtZSI6ImFkbWluIiwiZ2l2ZW5fbmFtZSI6IlJpY2FyZG8iLCJsb2NhbGUiOiJwdC1CUiIsImZhbWlseV9uYW1lIjoiQmF1bWFubiIsImVtYWlsIjoiZ2VtaW5pLnJpY2hhcmRAZ21haWwuY29tIn0.VObVSDOm6BVg0CNZKQdJKVf21WKH0QizIH453x0KJ4sLZRHHq-nYbnYFA8dQGWzproRvdqr35MxgeS7li4sFJL8WQpSxHZ4TeGHdtwzfetSP1juhv60IHW6Jl3G7_Tr5dDxRaxVIObTMMyJACtUaHqAiZDCNuBdovU9Sqy3XKpr81gC7Zj54euxK1p7C5T1-iigMcT2iLXZhdd-Ml0MwTvN8Y6grZHltlqSzxT8ehFN4GqGrJoBoAozkAR8zSda2FTKaJIkGo6ImvHXyzbsXc3tTz7iUMiYBBECDmuHeLAetMm28LQcXskmEUJlNPoQWIxc5w7huKu2rov00UNBOPg' \
  -H 'Content-Type: application/json' \
  -H 'Referer: http://localhost:3000/'