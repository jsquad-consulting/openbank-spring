#!/bin/bash

export MASTER_KEY=SECRET_JSQUAD_AB_KEY # For demo purpose only, normally only stored at the CI/hidden environment

export ROOT_PASSWORD=$(java -cp ~/.m2/repository/org/jasypt/jasypt/1.9.3/jasypt-1.9.3.jar \
org.jasypt.intf.cli.JasyptPBEStringDecryptionCLI \
input="kns7TCKMYQNS7XewfgrcvSD6Ml0IxaNb9+rI4IJDm1JODeX39WDbvA==" password=$MASTER_KEY algorithm=PBEWithMD5AndDES | tail -n3 | awk 'NF')

export OPENBANK_PASSWORD=$(java -cp ~/.m2/repository/org/jasypt/jasypt/1.9.3/jasypt-1.9.3.jar \
org.jasypt.intf.cli.JasyptPBEStringDecryptionCLI \
input="DSQpSyewyvrR8AurZvZC0DUax5oSZSqRvGmcAVFwKGcTZTnshKQiTw==" password=$MASTER_KEY algorithm=PBEWithMD5AndDES | tail -n3 | awk 'NF')


export SECURITY_PASSWORD=$(java -cp ~/.m2/repository/org/jasypt/jasypt/1.9.3/jasypt-1.9.3.jar \
org.jasypt.intf.cli.JasyptPBEStringDecryptionCLI \
input="vNq9+oOFXPNeXnQwwcPWWjMCgjNMOS5z2tt1TkE7WVxXK4bib+K8/w==" password=$MASTER_KEY algorithm=PBEWithMD5AndDES | tail -n3 | awk 'NF')