package fi.hel.allu.servicecore.domain;

import fi.hel.allu.common.domain.types.ApplicationType;

import java.util.Arrays;
import java.util.List;

import static fi.hel.allu.common.domain.types.ApplicationType.*;

public class StyleSheet {
  private static final List<ApplicationType> implementedTypes = Arrays.asList(
    EVENT,
    SHORT_TERM_RENTAL,
    CABLE_REPORT,
    PLACEMENT_CONTRACT,
    TEMPORARY_TRAFFIC_ARRANGEMENTS,
    EXCAVATION_ANNOUNCEMENT,
    AREA_RENTAL);

  // Get the stylesheet name to use for given application.
  public static String name(ApplicationJson application) {
    if (implementedTypes.contains(application.getType())) {
      return application.getType().name();
    } else {
      return "DUMMY";
    }
  }

  public static String name(ApplicationJson application, String suffix) {
    return name(application) + suffix;
  }
}
