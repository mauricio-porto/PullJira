#!/bin/bash

SCRIPT=$(basename $0)

usage() {
cat <<EOF

Usage: ${SCRIPT} <arquivo_a_importar>
e.g. 

    ${SCRIPT}  JiraLog.csv

EOF
}

error() {
   echo "" 
   echo "Error: $@" ; usage ; exit 1
}

ARQ=${1}
[ -z "${ARQ}" ] && error "Faltando o arquivo"
[ -r "${ARQ}" ] || error "Arquivo não encontrado"

emailUser=fulano@deliverit.com.br
passwordUser=senhaFulano

java -jar PullJira.jar ${emailUser} ${passwordUser} ${ARQ}

