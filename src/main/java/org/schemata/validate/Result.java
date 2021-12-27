package org.schemata.validate;

import java.util.List;


public record Result(Status status, List<String> errorMessages) {
}
