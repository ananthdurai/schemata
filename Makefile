
.PHONY:check_java
check_java:
ifeq (, $(shell which java))
 $(error "Schemata depends on Java17. JDK17 not found in $(PATH)")
endif

.PHONY: check_maven
check_maven:
ifeq (, $(shell which mvn))
 $(error "Schemata uses maven as a build tool. Maven not found in $(PATH)")
endif

.PHONY: compile
compile: check_java check_maven
	mvn clean compile

.PHONY: package
package: check_java check_maven
	mvn clean package

.PHONY: proto-gen
proto-gen:
	protoc --include_imports -I=./src/main/resources/schema --descriptor_set_out=./protos.desc --java_out=./src/main/java ./src/main/resources/schema/*.proto

.PHONY:validate
validate: proto-gen package
	java -jar target/schemata-1.0.jar schemata --cmd validate