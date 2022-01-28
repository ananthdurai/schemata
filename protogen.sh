#!/bin/bash
echo "generating descriptors for the protobuf definitions"
make proto-gen
if [ $? -eq 0 ]; then
  echo "protobuf descriptors generation is successful"
else
  echo "Error: Failed to generate protobuf descriptors"
fi
