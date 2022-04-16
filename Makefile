
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
	protoc  --proto_path=src/org --proto_path=src/main/resources/schema --descriptor_set_out=model.desc --include_imports --include_source_info ./src/main/resources/**/*.proto

.PHONY: build-all
build-all: package proto-gen