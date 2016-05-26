#!/bin/sh

docker run --privileged -e "MANAGER_URL=$1" -p 53452:53452 -v /var/run/docker.sock:/var/run/docker.sock dockyou-node
