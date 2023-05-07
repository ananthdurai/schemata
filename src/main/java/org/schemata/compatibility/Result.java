package org.schemata.compatibility;


import java.util.Set;

public record Result(Boolean isCompatible, Set<Summary> summary) {
}
