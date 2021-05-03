#!/usr/bin/env bash

main() {
    validateSqlFiles
}

validateSqlFiles() {
	docker run -v "${PWD}"/security/sql:/security flyway/flyway:latest \
	validate -url=jdbc:h2:mem:dummydb -locations=filesystem:/security -ignorePendingMigrations=true -X
	securitySqlValidation=$?

	docker run -v "${PWD}"/service/src/main/resources/db/migration/openbank:/openbank flyway/flyway:latest \
	validate -url=jdbc:h2:mem:dummydb -locations=filesystem:/openbank -ignorePendingMigrations=true -X
	openbankSqlFileValdation=$?

	if [[  "$securitySqlValidation" -eq 0 && "$openbankSqlFileValdation" -eq 0 ]]; then
		echo "Flyway validation successful!"
		exit 0
	else
		echo "Flyway validation failed!"
		exit 1
	fi
}

main "$*"