#!/bin/bash

absgpEmail=fulano@deliverit.com.br
absgpPwd=senhaFulano
jiraUsername=fulano
jiraPwd=senhaFulano
initialDate=2020-01-01
finalDate=2020-01-02

mvn clean package && java -jar target/PullJira.jar ${absgpEmail} ${absgpPwd} ${jiraUsername} ${jiraPwd} ${initialDate} ${finalDate}

