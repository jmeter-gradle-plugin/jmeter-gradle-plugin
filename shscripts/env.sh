#!/bin/bash
set -ev
echo -e "gradle.publish.key=$gradle_pub_KEY\ngradle.publish.secret=$gradle_pub_SECRET" > gradle.properties
export GRADLE_OPTS="-s"
