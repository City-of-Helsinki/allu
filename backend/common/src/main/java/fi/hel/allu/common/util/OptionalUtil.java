package fi.hel.allu.common.util;

import java.util.Optional;

public class OptionalUtil {
  public static <T> Optional<T> or(Optional<T> optional, Optional<T> fallback) {
    return optional.isPresent() ? optional : fallback;
  }
}
