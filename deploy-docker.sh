#!/bin/sh
docker build -t connect-be:latest .
aws ecr-public get-login-password --region us-east-1 | docker login --username AWS --password-stdin public.ecr.aws
docker tag connect-be:latest public.ecr.aws/l7g1e1z0/connect-be:latest
docker push public.ecr.aws/l7g1e1z0/connect-be:latest
q
