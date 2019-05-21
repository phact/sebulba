#!/bin/bash

set -x

rm -rf target/
rm -rf target.tar.gz
cd ui && npm run build
cd ..
ssh ubuntu@18.224.30.113 "rm -rf /target"
ssh ubuntu@18.224.30.113 "rm -rf target.tar.gz"

./mvnw clean package
tar -cvf target.tar.gz target/*

scp target.tar.gz ubuntu@18.224.30.113:
ssh ubuntu@18.224.30.113 "tar -xvf target.tar.gz"
ssh ubuntu@18.224.30.113 "ps -ef | grep sebulba | grep java | awk '{print $2}' | xargs kill"
ssh ubuntu@18.224.30.113 "nohup java -jar target/sebulba-1-runner.jar &"
