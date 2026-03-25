#!/bin/bash

echo "Stopping server..."

sudo docker kill SCServer

sudo docker rm SCServer

sudo docker compose down

echo "Server stopped."

exit
