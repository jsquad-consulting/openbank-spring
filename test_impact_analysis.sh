#!/usr/bin/env bash

main() {
    mainBranch=$1
    featureBranch=$2
    packageLevel=$3
    testFilesToExecute=""

    if [ "$#" -ne 3 ];
     then :
       echo "Illegal number of arguments, 2 separated branch names must be provided and Java package level."
       exit 1
    fi

	regexPattern='[A-Z][[:space:]]+.*\/main\/java\/(.*).java'

	editedJavaFiles="$(git diff --name-status "origin/${mainBranch}..origin/${featureBranch}" | while read line; do
		if [[ "${line}" =~ $regexPattern ]];
		then :
			editedJavaFiles="${editedJavaFiles} "`(echo "${BASH_REMATCH[1]}" | sed 's/\//./g')`
			editedJavaFiles=`(echo "${editedJavaFiles}" | xargs)`
			printf "${editedJavaFiles}"
		fi
    done)"

    filesDependentToCodeChanges=""

   for editedJavaFile in $editedJavaFiles; do
   		packageFiles=$(jdeps -e "${packageLevel}.+" -recursive -verbose . | sed '1d')
   		filesDependentToCodeChanges="$(findDependentJavaFilesToSpecificJavaFile "${editedJavaFile}" "${packageFiles}")"
   		filesDependentToCodeChanges="$(echo ${filesDependentToCodeChanges} | awk '{for (i=1;i<=NF;i++) if (!a[$i]++) printf("%s%s",$i,FS)}{printf("\n")}')"
	done

	regexPattern='([a-zA-Z0-9_.]+Test)'

	for fileDependentToCodeChange in $filesDependentToCodeChanges; do
		if [[ "${fileDependentToCodeChange}" =~ $regexPattern ]]; then
			testFilesToExecute="${testFilesToExecute} ${BASH_REMATCH[1]}"
		fi
	done

	testFilesToExecute="$(echo "${testFilesToExecute}" | xargs | sed 's/[[:space:]]/,/g')"
	if [ -z "${testFilesToExecute}" ]
	then
		printf "clean"
	else
		printf "test -Dtest=${testFilesToExecute}"
	fi
}

findDependentJavaFilesToSpecificJavaFile() {
	   	packageFileArray="${2}"

	   	packageFileArray=$(echo "${packageFileArray}" | sed 's/[[:space:]]\./\;/g')
	   	packageFileArray=$(echo "${packageFileArray}" | sed 's/[[:space:]]//g')
	    packageFileArray=$(echo "${packageFileArray}" | tr ";" "\n")

    	for packageFile in $packageFileArray; do
    	    editedJavaFileRegexpPattern="$(echo "${1}" | sed 's/\./\[.]/g')"
	   	    regexPattern='([a-zA-Z0-9_.]+)->('"${editedJavaFileRegexpPattern}"')'
			if [[ "${packageFile}" =~ $regexPattern ]];
			then :
				remainingLines=$(echo "${packageFileArray}" | sed "/${packageFile}/d")
				findDependentJavaFilesToSpecificJavaFile "${BASH_REMATCH[1]}" "${remainingLines}"
				printf " ${BASH_REMATCH[1]} "
			fi
    	done
    	printf " ${1} "
}

main $*