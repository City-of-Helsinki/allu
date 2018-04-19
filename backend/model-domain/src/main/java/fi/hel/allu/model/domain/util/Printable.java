package fi.hel.allu.model.domain.util;

import fi.hel.allu.model.domain.FixedLocation;
import fi.hel.allu.model.domain.PostalAddress;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Utilities for generating printable strings for various model-domain objects
 */
public class Printable {

  private static final String UNKNOWN_ADDRESS = "[Osoite ei tiedossa]";

  /**
   * Generate a printable string for a postal address
   */
  public static String forPostalAddress(PostalAddress postalAddress) {
    if (postalAddress == null) {
      return UNKNOWN_ADDRESS;
    }
    final String postalCodeAndCity = Arrays.asList(postalAddress.getPostalCode(), postalAddress.getCity()).stream()
        .filter(StringUtils::isNotEmpty)
        .collect(Collectors.joining(" "));
    return Arrays.asList(postalAddress.getStreetAddress(), postalCodeAndCity).stream()
        .filter(StringUtils::isNotEmpty)
        .collect(Collectors.joining(", "));
  }

  /**
   * Generate a printable string for a day period. If the days are the same or
   * either one is null, return string representing a single date, otherwise
   * return string "<start> - <end>". If both are null, return empty string.
   *
   * @param start
   * @param end
   * @return
   */
  public static String forDayPeriod(ZonedDateTime start, ZonedDateTime end) {
    if (start == null && end == null) {
      return "";
    }
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.uuuu");
    ZoneId helsinkiZone = ZoneId.of("Europe/Helsinki");
    LocalDate startDate = Optional.ofNullable(start).orElse(end).withZoneSameInstant(helsinkiZone).toLocalDate();
    LocalDate endDate = Optional.ofNullable(end).orElse(start).withZoneSameInstant(helsinkiZone).toLocalDate();
    if (startDate.compareTo(endDate) != 0) {
      return formatter.format(startDate) + " - " + formatter.format(endDate);
    } else {
      return formatter.format(startDate);
    }
  }

  public static String forFixedLocation(FixedLocation fixedLocation) {
    StringBuilder builder = new StringBuilder();
    if (fixedLocation != null) {
      builder.append(fixedLocation.getArea());
      if (fixedLocation.getSection() != null) {
        builder.append(", lohko ");
        builder.append(fixedLocation.getSection());
      }
    }
    return builder.toString();
  }

  public static String forFixedLocations(List<FixedLocation> fixedLocations) {
    final StringBuilder builder = new StringBuilder();
    for (FixedLocation fixedLocation : fixedLocations) {
      if (fixedLocation != null) {
        if (builder.length() == 0) {
          builder.append(fixedLocation.getArea());
        }
        if (fixedLocation.getSection() != null) {
          builder.append(", lohko ");
          builder.append(fixedLocation.getSection());
        }
      }
    }
    return builder.toString();
  }
}
