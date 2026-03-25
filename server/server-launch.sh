#!/bin/bash

echo "Stopping local postgresql service to open up a port"

sudo service postgresql stop

echo "Starting database container"

sudo docker compose up -d

echo "Populating database"

sudo docker exec -i postgres psql db -U kottsov -c "CREATE DATABASE scserverdata"

sudo docker exec -i postgres psql db -U kottsov -c "CREATE DATABASE scserverdata_test"

sudo docker cp scserverdata.sql postgres:/tmp/scserverdata.sql

sudo docker cp scserverdata_test.sql postgres:/tmp/scserverdata_test.sql

sudo docker exec -i postgres psql -U kottsov -d scserverdata -f /tmp/scserverdata.sql

sudo docker exec -i postgres psql -U kottsov -d scserverdata_test -f /tmp/scserverdata_test.sql

echo "Building..."

sudo docker build -t server .

echo "Starting"

sudo docker run -d --network="host" --name="SCServer" server:latest

exit
