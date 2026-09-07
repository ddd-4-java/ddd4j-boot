#!/usr/bin/env bash

set -euo pipefail

repository=${1:-snapshot}
repo_root=$(cd "$(dirname "$0")/.." && pwd -P)
version=$("$repo_root/mvnw" -q -N -B help:evaluate -Dexpression=project.version -DforceStdout \
  | sed -n 's/^\[INFO\] \[stdout\] //p' | tail -1)

case "$repository" in
  snapshot)
    repository_id=2624322-snapshot-3EoOv3
    repository_url=https://packages.aliyun.com/6927b116e6c3e0425dbdf60d/maven/2624322-snapshot-3eoov3
    ;;
  release)
    repository_id=2624322-release-6F6h6R
    repository_url=https://packages.aliyun.com/6927b116e6c3e0425dbdf60d/maven/2624322-release-6f6h6r
    ;;
  *)
    echo "ERROR: repository must be snapshot or release" >&2
    exit 1
    ;;
esac

if ! mvn -version | head -1 | grep -Eq 'Apache Maven 3\.'; then
  echo "ERROR: the Maven 4 upload bridge requires Maven 3 on PATH" >&2
  exit 1
fi

stage_dir=$(mktemp -d "${TMPDIR:-/tmp}/ddd4j-boot-deploy-${version}.XXXXXX")
artifact_list="$stage_dir/artifacts.txt"

find "$HOME/.m2/repository/io/ddd4j/boot" \
  -mindepth 3 -maxdepth 3 -type f -name "*-${version}.pom" \
  -path "*/${version}/*" -print \
  | sed -E 's#/[^/]+/[^/]+$##; s#.*/##' \
  | sort -u > "$artifact_list"

if [[ ! -s "$artifact_list" ]]; then
  echo "ERROR: no installed ddd4j-boot ${version} artifacts were found" >&2
  exit 1
fi

export version stage_dir repository_id repository_url
xargs -P 4 -I '{}' bash -c '
  set -euo pipefail
  artifact_id=$1
  local_dir="$HOME/.m2/repository/io/ddd4j/boot/$artifact_id/$version"
  pom="$local_dir/$artifact_id-$version.pom"
  jar="$local_dir/$artifact_id-$version.jar"
  sources="$local_dir/$artifact_id-$version-sources.jar"
  cp "$pom" "$stage_dir/$artifact_id.pom"
  cd "$stage_dir"
  if [[ -f "$jar" ]]; then
    cp "$jar" "$stage_dir/$artifact_id.jar"
    attachments=()
    if [[ -f "$sources" ]]; then
      cp "$sources" "$stage_dir/$artifact_id-sources.jar"
      attachments=(
        "-Dfiles=$stage_dir/$artifact_id-sources.jar"
        -Dclassifiers=sources
        -Dtypes=jar
      )
    fi
    mvn -q -B org.apache.maven.plugins:maven-deploy-plugin:3.1.4:deploy-file \
      "-Dfile=$stage_dir/$artifact_id.jar" \
      "-DpomFile=$stage_dir/$artifact_id.pom" \
      -DgroupId=io.ddd4j.boot \
      "-DartifactId=$artifact_id" \
      "-Dversion=$version" \
      -Dpackaging=jar \
      "${attachments[@]}" \
      "-DrepositoryId=$repository_id" \
      "-Durl=$repository_url"
  else
    mvn -q -B org.apache.maven.plugins:maven-deploy-plugin:3.1.4:deploy-file \
      "-Dfile=$stage_dir/$artifact_id.pom" \
      "-DpomFile=$stage_dir/$artifact_id.pom" \
      -DgroupId=io.ddd4j.boot \
      "-DartifactId=$artifact_id" \
      "-Dversion=$version" \
      -Dpackaging=pom \
      "-DrepositoryId=$repository_id" \
      "-Durl=$repository_url"
  fi
  echo "DEPLOYED $artifact_id:$version"
' bash '{}' < "$artifact_list"
