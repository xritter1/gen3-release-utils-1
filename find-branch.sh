#!/bin/bash
repos_to_check=(arborist gen3-statics fence cdis-data-client guppy hatchery indexd manifestservice pelican peregrine pidgin data-portal sheepdog sower ssjdispatcher tube workspace-token-service gen3-fuse indexs3client gen3-spark metadata-service docker-nginx)
cd "/Users/${USER}/workspace"
for repo in ${repos_to_check[@]}
do
  echo "stepping into: ${repo}"
  cd $repo
  git fetch
  found=$(git branch -a | grep "integration202004")
  RC=$?
  if [ $RC -ne 0 ]; then
    echo "repo [${repo}] does not have the 'integration' branch. Failed."
    exit 1
  else
    echo "FOUND IT!!!"
  fi
  cd ..
done
