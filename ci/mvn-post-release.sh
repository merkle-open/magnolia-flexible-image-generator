#!/usr/bin/env bash

CURRENT_VERSION=`xmllint --xpath '/*[local-name()="project"]/*[local-name()="version"]/text()' pom.xml`

if [[ $CURRENT_VERSION != *-SNAPSHOT ]]; then
	NEXT_VERSION=`bash ci/semver.sh -p CURRENT_VERSION`
	NEXT_SNAPSHOT="$NEXT_VERSION-SNAPSHOT"

  mvn versions:set -DnewVersion=$NEXT_SNAPSHOT versions:commit --no-transfer-progress

	echo "commit new snapshot version"
	git commit -a -m "Release $NEW_VERSION: set 6.2 to next development version $NEXT_SNAPSHOT"

	git push --all
fi
