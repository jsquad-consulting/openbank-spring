#!/bin/bash

#
# Copyright 2021 JSquad AB
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

wget -q -O - https://raw.githubusercontent.com/rancher/k3d/main/install.sh | TAG=v3.4.0 bash

k3d cluster create jsquad --api-port 6443 --port 1080:1080@loadbalancer --port 80:80@loadbalancer \
--port 443:443@loadbalancer --k3s-server-arg "--no-deploy=traefik"

helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm install my-nginx ingress-nginx/ingress-nginx

helm upgrade --install --namespace default --set service.type=LoadBalancer --set service.port=1080 \
mockserver http://www.mock-server.com/mockserver-5.11.1.tgz

./verify_deployed_service.sh mockserver

NODE_PORT=$(kubectl get -n default -o jsonpath="{.spec.ports[0].nodePort}" services mockserver)
NODE_IP=$(kubectl get nodes -n default -o jsonpath="{.items[0].status.addresses[0].address}")

export MOCKSERVER_HOST=$NODE_IP:$NODE_PORT

kubectl create secret generic openbank-spring-secret --from-literal=MASTER_KEY="$MASTER_KEY" \
--from-literal=ROOT_PASSWORD="$ROOT_PASSWORD" --from-literal=OPENBANK_PASSWORD="$OPENBANK_PASSWORD" \
--from-literal=SECURITY_PASSWORD="$SECURITY_PASSWORD" --from-literal=WORLD_API_HOST_AND_PORT="$MOCKSERVER_HOST"

kubectl create configmap ssl-volume --from-file=service/src/test/resources/test/ssl/truststore
kubectl create configmap flyway-ddl-volume --from-file=security/sql

kubectl apply -f deployment/openbank_postgres_volume.yaml
kubectl apply -f deployment/security_postgres_volume.yaml

kubectl apply -f deployment/openbankdb.yaml
kubectl apply -f deployment/securitydb.yaml

kubectl apply -f deployment/openbankdb_service.yaml
kubectl apply -f deployment/securitydb_service.yaml

./verify_deployed_service.sh openbankdb-deployment
./verify_deployed_service.sh securitydb-deployment

kubectl apply -f deployment/flyway.yaml

kubectl apply -f deployment/openbank_spring.yaml
kubectl apply -f deployment/openbank_spring_service.yaml
kubectl apply -f deployment/ingress.yaml

./verify_deployed_service.sh openbank-spring-deployment
