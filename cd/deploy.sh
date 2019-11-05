#!/usr/bin/env bash
#
# This script will deploy artifacts of the project.
#
set -e

CD_DIR=$(cd "$(dirname "$0")"; pwd)
ROOT_DIR=${CD_DIR}/../
MVNW="${ROOT_DIR}/mvnw -B"

RELEASE_TYPE=${1:-snapshot}
VERSION="$2"
MAVEN_RELEASE_SETTINGS=${CD_DIR}/settings.xml


if [ "${RELEASE_TYPE}" == "release" ] && [ ! -z "${VERSION}" ]; then
  echo -e "[INFO] on a tag -> set pom.xml <version> to ${VERSION}"
  ${MVNW} versions:set -DnewVersion=${VERSION}
else
  echo -e "[INFO] not on a tag -> keep snapshot version in pom.xml"
fi

${MVNW} source:jar deploy -s ${MAVEN_RELEASE_SETTINGS} -DskipTests=true -P publishing

