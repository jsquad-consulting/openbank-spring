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

    dependentFiles=$editedJavaFiles

   for editedJavaFile in $editedJavaFiles; do
   	    packageFiles=$(jdeps -e "${packageLevel}.+" -recursive -verbose .)
   		editedJavaFileRegexpPattern="$(echo ${editedJavaFile} | sed 's/\./\[.]/g')"
   		dependentFiles="${dependentFiles} $(findDependentJavaFilesToSpecificJavaFile "${editedJavaFileRegexpPattern}" "${packageFiles}")"
   		dependentFiles="$(echo ${dependentFiles} | awk '{for (i=1;i<=NF;i++) if (!a[$i]++) printf("%s%s",$i,FS)}{printf("\n")}')"
	done

	regexPattern='[[:space:]]?([a-zA-Z0-9_.]+Test)[[:space:]]?'

	testFilesToExecute="$(echo "${dependentFiles}" | while read line;
		do
		if [[ "${line}" =~ $regexPattern ]]; then

				printf "${testFilesToExecute} ${BASH_REMATCH[1]}"
			fi
		done)"

	testFilesToExecute="$(echo "${testFilesToExecute}" | xargs | sed 's/[[:space:]]/,/g')"
	if [ -z "${testFilesToExecute}" ]
	then
		printf ""
	else
		printf "test -Dtest=${testFilesToExecute}"
	fi
}

findDependentJavaFilesToSpecificJavaFile() {
	   	regexPattern='([a-zA-Z0-9_.]+)[[:space:]]+->[[:space:]]+('"${1}"')'
	    dependentFiles="$(echo "${2}" | while read line;
    	do
   		if [[ "${line}" =~ $regexPattern ]];
			then :
			    remainingLines=$(echo "${dependentFiles}" | sed -n "/${line}/d")
				findDependentJavaFilesToSpecificJavaFile ${BASH_REMATCH[1]} ${remainingLines}
				printf "${dependentFiles} ${BASH_REMATCH[1]}"
			fi
    	done)"
    	printf "${dependentFiles}"
}

main $*