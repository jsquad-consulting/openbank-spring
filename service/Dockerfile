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

FROM openjdk:11-jre-slim

COPY target/openbank-service-0.0.1-SNAPSHOT.jar /app/openbank-spring.jar

EXPOSE 8443 8081 8000

CMD ["bash", "-c", "java -jar /app/openbank-spring.jar --jasypt.encryptor.password=${MASTER_SECRET} \
--spring.config.location=${CONFIG_FILE_LOCATIONS}"]