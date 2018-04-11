#!/bin/bash
mvn clean package -DskipTests=true
scp target/qna-0.0.1-SNAPSHOT.jar root@47.104.183.9:~