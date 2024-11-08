#!/usr/bin/env bash

CURRENT_VERSION=`xmllint --xpath '/*[local-name()="project"]/*[local-name()="version"]/text()' pom.xml`

if [[ $CURRENT_VERSION == *-6.2 ]]; then
	NEW_VERSION=${CURRENT_VERSION%'-6.2'}
	NEXT_VERSION=`bash ci/semver.sh -p $NEW_VERSION`
	NEXT_SNAPSHOT="$NEXT_VERSION-6.2"
	echo "perform release of $NEW_VERSION from $CURRENT_VERSION and set next develop version $NEXT_SNAPSHOT"

	mvn versions:set -DnewVersion=$NEW_VERSION versions:commit --no-transfer-progress

 	echo "commit new release version"
	git commit -a -m "Release $NEW_VERSION: set 6.2 to new release version"

	echo "Update version in README.md"
	sed -i -e "s|<version>[0-9A-Za-z._-]\{1,\}</version>|<version>$NEW_VERSION</version>|g" README.md && rm -f README.md-e
	git commit -a -m "Release $NEW_VERSION: Update README.md"

	echo "create tag for new release"
	git tag -a $NEW_VERSION -m "Release $NEW_VERSION: tag release"

	mvn versions:set -DnewVersion=$NEXT_SNAPSHOT versions:commit --no-transfer-progress

	git push --all
	git push --tags
fi
