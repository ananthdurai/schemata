#!/bin/bash
java -jar target/schemata-1.0.jar score -s=src/test/resources/descriptors/entities.desc -p=PROTOBUF $1
