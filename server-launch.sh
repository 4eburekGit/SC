#!/bin/bash

echo "Stopping local postgresql service to open up a port"

sudo service postgresql stop

echo "Starting database container"

sudo docker compose up -d

echo "Building..."

sudo docker build -t server .

echo "Starting"

sudo docker run -d --network="host" --name="SCServer" server:latest

exit
