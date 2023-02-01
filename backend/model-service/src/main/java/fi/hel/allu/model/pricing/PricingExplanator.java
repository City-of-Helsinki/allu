package fi.hel.allu.model.pricing;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.dao.LocationDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.FixedLocation;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.model.domain.util.Printable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Helper class for generating pricing explanation lines based on location.
 */
@Service
public class PricingExplanator {
  private static final int EXPLANATION_MAX_LENGTH = 70;
  private static final Locale DEFAULT_LOCALE = new Locale("fi", "FI");

  private final LocationDao locationDao;

  @Autowired
  public PricingExplanator(LocationDao locationDao) {
    this.locationDao = locationDao;
  }

  public List<String> getExplanation(Application application) {
    return formatExplanation(application, null);
  }

  public List<String> getExplanation(Location location, Integer fixedLocationId) {
    final FixedLocation fixedLocation = locationDao.findFixedLocation(fixedLocationId)
        .orElseThrow(() -> new NoSuchEntityException("Fixed location not found", fixedLocationId));
    final String fixedLocationAddress = Printable.forFixedLocation(fixedLocation);
    final String period = Printable.forDayPeriod(location.getStartTime(), location.getEndTime());
    return limitExplanationRowLength(fixedLocationAddress + " (" + period + ")");
  }

  public List<String> getExplanationWithCustomPeriod(Application application, String customPeriod) {
    return formatExplanation(application, customPeriod);
  }

  public List<String> getExplanation(Location location, String period) {
    return formatExplanation(location, period);
  }

  private List<String> formatExplanation(Application application, String customPeriod) {
    if (application.getId() == null) {
      return Collections.emptyList();
    }

    List<Location> locations = locationDao.findByApplicationId(application.getId());
    if (locations.size() > 1) {
      throw new RuntimeException("Only one location is supported in PricingExplanationService");
    } else if (locations.isEmpty()) {
      return Collections.emptyList();
    }
    // Check type also, as excavations may have multiple kinds and getKind() throws an exception if more than 1 exist
    if (ApplicationType.SHORT_TERM_RENTAL.equals(application.getType()) && ApplicationKind.PARKLET.equals(application.getKind())) {
      return formatExplanation(locations.get(0), true, customPeriod);
    }
    return formatExplanation(locations.get(0), customPeriod);
  }

  private List<String> formatExplanation(Location location, String customPeriod) {
    return formatExplanation(location, false, customPeriod);
  }

  private List<String> formatExplanation(Location location, boolean includePaymentClass, String customPeriod) {
    final List<FixedLocation> fixedLocations = new ArrayList<>();
    fixedLocations.addAll(locationDao.getFixedLocations(location.getFixedLocationIds()));
    final String fixedLocation = Printable.forFixedLocations(fixedLocations);
    final String locationAddress = Printable.forPostalAddress(location.getPostalAddress());
    final String address = fixedLocation.length() > 0 ? fixedLocation : locationAddress;

    final String paymentClass = includePaymentClass ?
      String.format(DEFAULT_LOCALE, ", maksuvyöhyke %s", location.getEffectivePaymentTariff()) : "";

    final String period = customPeriod != null ? customPeriod :
        Printable.forDayPeriod(location.getStartTime(), location.getEndTime());

    final String area = ((int)Math.ceil(location.getEffectiveArea())) + "m²";

    final String explanation = address + paymentClass + " (" + period + "), " + area;
    return limitExplanationRowLength(explanation);
  }

  private List<String> limitExplanationRowLength(String explanation) {
    final List<String> explanations = new ArrayList<>();
    if (explanation.length() > EXPLANATION_MAX_LENGTH) {
      StringBuilder builder = new StringBuilder();
      final String[] splits = explanation.split(" ");
      for (String split : splits) {
        if (builder.length() == 0) {
          builder.append(split);
        } else if (builder.length() + splits.length + 1 <= 70) {
          builder.append(" ");
          builder.append(split);
        } else {
          explanations.add(builder.toString());
          builder = new StringBuilder();
          builder.append(split);
        }
      }
      if (builder.length() > 0) {
        explanations.add(builder.toString());
      }
    } else {
      explanations.add(explanation);
    }
    return explanations;
  }
}