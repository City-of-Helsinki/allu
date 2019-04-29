package fi.hel.allu.model.domain.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import fi.hel.allu.model.domain.FixedLocation;
import fi.hel.allu.model.domain.PostalAddress;

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
        builder.append(" - ");
        builder.append(fixedLocation.getSection());
      }
    }
    return builder.toString();
  }

  public static String forFixedLocations(List<FixedLocation> fixedLocations) {
    Map<String, List<String>> areasWithSections = getAreasWithSections(fixedLocations);
    return forAreasWithSections(areasWithSections);
  }

  public static String forAreasWithSections(Map<String, List<String>> areasWithSections) {
    List<String> areasWithSectionsStrs = areasWithSections
        .entrySet()
        .stream()
        .map(a -> getAreaString(a))
        .collect(Collectors.toList());
    return String.join(", ", areasWithSectionsStrs);
  }

  private static Map<String, List<String>> getAreasWithSections(List<FixedLocation> fixedLocations) {
    return fixedLocations.stream().
        collect(Collectors.groupingBy(FixedLocation::getArea,
            Collectors.mapping(FixedLocation::getSection, Collectors.toList())));
  }

  public static String getAreaString(Entry<String, List<String>> areaWithSections) {
    final StringBuilder builder = new StringBuilder();
    builder.append(areaWithSections.getKey());
    List<String> sections = areaWithSections.getValue().stream()
        .filter(s -> s != null)
        .collect(Collectors.toList());
    if (sections.size() > 0) {
      builder.append(" - ");
      builder.append(String.join(", ", sections));
    }
    return builder.toString();
  }
}
