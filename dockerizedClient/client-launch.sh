#!/bin/bash

echo "Accesing display..."

xhost local:root

echo "Building client..."

sudo docker build -t kottsov/scclient .

echo "Starting client..."

sudo docker run --network="host" -v /tmp/.X11-unix:/tmp/.X11-unix -e DISPLAY=$DISPLAY docker.io/kottsov/scclient:latest

exit
