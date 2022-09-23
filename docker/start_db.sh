#!/bin/bash

# EXAMPLE:
# ./start_db.sh

DOCKER_IMAGE="docker.io/arangodb/arangodb:latest"
STARTER_DOCKER_IMAGE="docker.io/arangodb/arangodb-starter:latest"
GW=172.28.0.1
docker network create arangodb --subnet 172.28.0.0/16

# exit when any command fails
set -e

docker pull $STARTER_DOCKER_IMAGE
docker pull $DOCKER_IMAGE

docker run -d \
    --name=adb \
    -p 8528:8528 \
    -v /var/run/docker.sock:/var/run/docker.sock \
    $STARTER_DOCKER_IMAGE \
    --docker.container=adb \
    --starter.address="${GW}" \
    --docker.image="${DOCKER_IMAGE}" \
    --starter.local --starter.mode=single --all.log.level=debug --all.log.output=+ --log.verbose

wait_server() {
    # shellcheck disable=SC2091
    until $(curl --output /dev/null --insecure --fail --silent --head -i "http://$1/_api/version"); do
        printf '.'
        sleep 1
    done
}

echo "Waiting..."
wait_server "127.0.0.1:8529"

echo ""
echo ""
echo "Done, your deployment is reachable at: "
echo "http://127.0.0.1:8529"
echo ""
