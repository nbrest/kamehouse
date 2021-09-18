#!/bin/bash

CONTAINER=$1

if [ -n "${CONTAINER}" ]; then 
  docker stop ${CONTAINER}
else
  docker stop kamehouse-docker
fi
