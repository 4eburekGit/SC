#!/bin/bash

echo "Stopping server..."

sudo docker kill SCServer

sudo docker rm SCServer

echo "Server stopped."

exit
