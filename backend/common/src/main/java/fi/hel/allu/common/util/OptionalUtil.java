package fi.hel.allu.common.util;

import java.util.Optional;
import java.util.function.Supplier;

public class OptionalUtil {
  public static <T> Optional<T> or(Optional<T> optional, Supplier<Optional<T>> fallback) {
    return optional.isPresent() ? optional : fallback.get();
  }

  public static <T> Optional<T> or(Optional<T> optional, Supplier<Optional<T>> fallback, Supplier<Optional<T>> fallback2) {
    Optional<T> result = or(optional, fallback);
    return result.isPresent() ? result : fallback2.get();
  }
}
