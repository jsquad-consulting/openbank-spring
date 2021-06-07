#!/bin/bash

#
# Copyright 2021 JSquad AB
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

main() {
	minimum_coverage_percentage=$1

	find ./target/site -name "jacoco.xml" | grep .

	if [[ $? -ne 0 ]]; then
		echo "Failed to generate JaCoCo xml report!"
		exit 1
	fi

	regexPattern='<counter type="INSTRUCTION" missed="([0-9]+)" covered="([0-9]+)"\/>'

	content=$(cat ./target/site/jacoco.xml | xmllint --format - | grep -i '<counter type="INSTRUCTION" missed="' | tail -1)

	if [[ $content =~ $regexPattern ]]; then
		missed_instructions="${BASH_REMATCH[1]}"
		covered_instructions="${BASH_REMATCH[2]}"
	else
		echo "Failed to match regular expression pattern of ${regexPattern}."
		exit 1
	fi

	total_instructions=$(echo "$missed_instructions+$covered_instructions" | bc)
	covered_instructions=$(echo "scale=4; $covered_instructions/$total_instructions" | bc)
	covered_instructions_in_percentage=$(echo "$covered_instructions*100" | bc)

	echo "Code coverage is at $covered_instructions_in_percentage percent."

	if (( $(echo "$covered_instructions_in_percentage < $minimum_coverage_percentage" | bc -l) )); then
		echo "Coverage is less then $minimum_coverage_percentage percent."
		exit 1
	fi
}
main $*
