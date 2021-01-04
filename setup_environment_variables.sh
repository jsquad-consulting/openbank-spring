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

MASTER_KEY=SECRET_JSQUAD_AB_KEY # For demo purpose only, normally only stored at the CI/hidden environment

ROOT_PASSWORD=$(java -cp ~/.m2/repository/org/jasypt/jasypt/1.9.3/jasypt-1.9.3.jar \
org.jasypt.intf.cli.JasyptPBEStringDecryptionCLI \
input="kns7TCKMYQNS7XewfgrcvSD6Ml0IxaNb9+rI4IJDm1JODeX39WDbvA==" password=$MASTER_KEY algorithm=PBEWithMD5AndDES | tail -n3 | awk 'NF')

OPENBANK_PASSWORD=$(java -cp ~/.m2/repository/org/jasypt/jasypt/1.9.3/jasypt-1.9.3.jar \
org.jasypt.intf.cli.JasyptPBEStringDecryptionCLI \
input="DSQpSyewyvrR8AurZvZC0DUax5oSZSqRvGmcAVFwKGcTZTnshKQiTw==" password=$MASTER_KEY algorithm=PBEWithMD5AndDES | tail -n3 | awk 'NF')

SECURITY_PASSWORD=$(java -cp ~/.m2/repository/org/jasypt/jasypt/1.9.3/jasypt-1.9.3.jar \
org.jasypt.intf.cli.JasyptPBEStringDecryptionCLI \
input="vNq9+oOFXPNeXnQwwcPWWjMCgjNMOS5z2tt1TkE7WVxXK4bib+K8/w==" password=$MASTER_KEY algorithm=PBEWithMD5AndDES | tail -n3 | awk 'NF')

export MASTER_KEY ROOT_PASSWORD OPENBANK_PASSWORD SECURITY_PASSWORD