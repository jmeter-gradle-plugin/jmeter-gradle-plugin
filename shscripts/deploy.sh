#!/bin/bash
set -ev
if[ "${TRAVIS_BRANCH}" = "release"] then
    gradle bintrayupload publishPlugins
else
    gradle artifactoryPublish
fi