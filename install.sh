#!/bin/bash

# Check if a version parameter is provided; if not default to v1

if [ "$#" -eq 1 ]; then
  version="$1"
else
  version="v1"
fi

# Build Schemata Directory for Check In

SCHEMATA_DIR="opencontract/${version}/org/schemata/protobuf"

mkdir -p $SCHEMATA_DIR

# Download Schemata proto

curl -L -o $SCHEMATA_DIR/schemata.proto https://raw.githubusercontent.com/ananthdurai/schemata/main/src/opencontract/v1/org/schemata/protobuf/schemata.proto

echo "Successfully downloaded the folder: $SCHEMATA_DIR"
