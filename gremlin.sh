#!/bin/bash
echo "docker-compose exec janusgraph ./bin/gremlin.sh $@"
docker-compose exec janusgraph ./bin/gremlin.sh $@