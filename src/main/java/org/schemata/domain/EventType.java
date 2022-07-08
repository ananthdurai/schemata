package org.schemata.domain;

import java.util.Arrays;


public enum EventType {
  NONE, LIFECYCLE, ACTIVITY, AGGREGATED;

  public static EventType get(String eventType) {
    return Arrays.stream(EventType.values()).filter(e -> e.name().equalsIgnoreCase(eventType)).findAny()
        .orElse(EventType.NONE);
  }
}
