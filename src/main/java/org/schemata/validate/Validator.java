package org.schemata.validate;

import java.util.Optional;
import java.util.function.Predicate;


public interface Validator<T> {

  default Optional<String> test(Rules rule, Predicate<T> predicate, T t) {
    return predicate.test(t) ? Optional.of(rule.errorMessage) : Optional.empty();
  }
}
