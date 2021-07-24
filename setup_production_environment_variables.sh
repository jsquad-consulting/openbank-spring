#!/usr/bin/env bash
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

MASTER_KEY=$PROD_MASTER_KEY

SSL_ENCRYPTED_PASSWORD="zDleNj2j6+QvzxWxAAKT9u65SFOzu2PF"

ROOT_PASSWORD=$(java -cp ~/.m2/repository/org/jasypt/jasypt/1.9.3/jasypt-1.9.3.jar \
org.jasypt.intf.cli.JasyptPBEStringDecryptionCLI \
input="VmVV6iO8PUjIG+opClX4tMdH6bt5YgpFnkyMQr0nmBWhZmn7ZnCmHg==" password=$MASTER_KEY algorithm=PBEWithMD5AndDES | tail -n3 | awk 'NF')

OPENBANK_PASSWORD=$(java -cp ~/.m2/repository/org/jasypt/jasypt/1.9.3/jasypt-1.9.3.jar \
org.jasypt.intf.cli.JasyptPBEStringDecryptionCLI \
input="EOKoo1X2rkgJ3TWqZdmEnvf26B7Pox31DyCxmIGR0g7AHxuBF0b8tw==" password=$MASTER_KEY algorithm=PBEWithMD5AndDES | tail -n3 | awk 'NF')

SECURITY_PASSWORD=$(java -cp ~/.m2/repository/org/jasypt/jasypt/1.9.3/jasypt-1.9.3.jar \
org.jasypt.intf.cli.JasyptPBEStringDecryptionCLI \
input="+TO9xz4lgbNususDouCXaAsfJsUxh8+pqPa/lphrUyH1TBt/rfxBdnqYn+p9pTPk" password=$MASTER_KEY algorithm=PBEWithMD5AndDES | tail -n3 | awk 'NF')

export MASTER_KEY ROOT_PASSWORD OPENBANK_PASSWORD SECURITY_PASSWORD SSL_ENCRYPTED_PASSWORD
