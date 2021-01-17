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

# SECONDS is internal shell clock timer
SECONDS=0
TIMEOUT=300
RETRY_INTERVAL=10

serviceReady="False"

while [ "$serviceReady" != "True" ] && [ $SECONDS -lt $TIMEOUT ];
do
	statusCode=$(curl -o /dev/null -I -w "%{http_code}" -X GET "http://localhost/actuator/shallowhealth")
	if [ "$statusCode" -ne 200 ];
	then :
		echo -n " ${SECONDS}"
		sleep $RETRY_INTERVAL
	else
		serviceReady="True"
	fi
done

statusCodeHttpEndPoint=$(curl -o /dev/null -I -w "%{http_code}"  -X GET "http://localhost/actuator/shallowhealth")
statusCodeHttpsEndpoint=$(curl -o /dev/null -I -w "%{http_code}" -kX GET "https://localhost/api/client/info/191212121212")

if [ "$statusCodeHttpEndPoint" -ne 200 ] || [ "$statusCodeHttpsEndpoint" -ne 200 ]; then
	exit 1
fi

exit 0