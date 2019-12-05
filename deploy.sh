#!/bin/bash
read -p "Enter Bintray Username: "  bintrayUser
read -s -p "Enter Bintray Password: "  bintrayKey

./gradlew clean build bintrayUpload -PbintrayUser=${bintrayUser} -PbintrayKey=${bintrayKey} -PdryRun=false