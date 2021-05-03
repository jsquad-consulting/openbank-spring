#!/usr/bin/env bash

main() {
    if [[ "$#" -ne 1 ]];
     then :
       echo "Illegal number of arguments, branch name must be provided."
       exit 1
    fi

    isThereFileChangesBetweenFeatureBranchAndMasterBranch "$1" "SQL" "security service/src/main/resources/db/migration" \
    "both"

    isThereFileChangesBetweenFeatureBranchAndMasterBranch "$1" "pipeline" ".travis.yml" \
    "master"
}

isThereFileChangesBetweenFeatureBranchAndMasterBranch() {
    branch="$1"
    message="$2"
    files_or_paths="$3"
    operation="$4"

    git fetch --all

    numberOfFileChangesBetweenBranches=$(git rev-list --left-right --count \
    origin/master...origin/"${branch}" -- "${files_or_paths}")

    if [ $? -ne 0 ];
     then :
       echo "Failed to get the revision list from the origin remote branches."
       exit 1
    fi

    regexPattern="([0-9]+)\s+([0-9]+)"

    if [[ "${numberOfFileChangesBetweenBranches}" =~ $regexPattern ]];
    then :
		numberOfMasterBranchFileChanges="${BASH_REMATCH[1]}"
		numberOfFeatureBranchFileChanges="${BASH_REMATCH[2]}"
    fi

    if [ "${operation}" != "both" ] && [[ "${numberOfMasterBranchFileChanges}" -gt 0 ]];
    then :
      echo "Feature branch ${branch} is behind master branch with ${message} file changes. Please rebase your branch and try again."
      exit 1
    elif [ "${operation}" == "both" ] && [[ "${numberOfMasterBranchFileChanges}" -gt 0 ]] && [[ "${numberOfFeatureBranchFileChanges}" -gt 0 ]];
    then :
      echo "Feature branch ${branch} and master branch has file changes with ${message} files. Please rebase your branch and try again."
      exit 1
    else
       echo "Feature branch ${branch} is OK and not behind master with ${message} file changes, rebase is not needed."
    fi
}

main "$*"