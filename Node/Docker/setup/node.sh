#!/bin/sh

docker run --privileged -e "MANAGER_URL=$1" dockyou-node -p 53452:53452
