#!/bin/bash

set -e

CUR_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

MAJOR=$(echo $CUR_VERSION | cut -d '.' -f 1)
FEATURE=$(echo $CUR_VERSION | cut -d '.' -f 2)
PATCH=$(echo $CUR_VERSION | cut -d '.' -f 3 | cut -d '-' -f 1)

if test -n "$(find changelogs/major -name '*.json' -print -quit)"; then
  MAJOR=$(($MAJOR+1))
elif test -n "$(find changelogs/feature -name '*.json' -print -quit)"; then
  FEATURE=$(($FEATURE+1))
else
  PATCH=$(($PATCH+1))
fi

echo "$MAJOR.$FEATURE.$PATCH"
exit 0
