.ONESHELL:
.SILENT:
SHELL=cmd

run-school:
	cd planning-school
	./mvnw clean spring-boot:run  -D"spring-boot.run.jvmArguments"="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8787"

test-school:
	cd planning-school
	./mvnw clean test

run-api:
	cd planning-api
	./mvnw quarkus:dev

test-api:
	cd planning-api
	./mvnw clean test

generate-client-api:
	cd planning-client-api
	./mvnw clean install