package fi.hel.allu.model.pricing;

import fi.hel.allu.model.dao.LocationDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.FixedLocation;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.model.domain.util.Printable;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Helper class for generating pricing explanation lines based on location.
 */
public class PricingExplanator {
  private static final int EXPLANATION_MAX_LENGTH = 70;

  private final LocationDao locationDao;

  @Autowired
  public PricingExplanator(LocationDao locationDao) {
    this.locationDao = locationDao;
  }

  public List<String> getExplanation(Application application) {
    return formatExplanation(application, null);
  }

  public List<String> getExplanationWithCustomPeriod(Application application, String customPeriod) {
    return formatExplanation(application, customPeriod);
  }

  public List<String> getExplanation(Location location) {
    return formatExplanation(location, null);
  }

  private List<String> formatExplanation(Application application, String customPeriod) {
    if (application.getId() == null) {
      return Collections.emptyList();
    }

    List<Location> locations = locationDao.findByApplication(application.getId());
    if (locations.size() > 1) {
      throw new RuntimeException("Only one location is supported in PricingExplanationService");
    } else if (locations.isEmpty()) {
      return Collections.emptyList();
    }
    return formatExplanation(locations.get(0), customPeriod);
  }

  private List<String> formatExplanation(Location location, String customPeriod) {
    final List<FixedLocation> fixedLocations = new ArrayList<>();
    location.getFixedLocationIds().forEach((id) -> locationDao.findFixedLocation(id).map(fl -> fixedLocations.add(fl)));
    final String fixedLocation = Printable.forFixedLocations(fixedLocations);
    final String locationAddress = Printable.forPostalAddress(location.getPostalAddress());
    final String address = fixedLocation.length() > 0 ? fixedLocation : locationAddress;

    final String period = customPeriod != null ? customPeriod :
        Printable.forDayPeriod(location.getStartTime(), location.getEndTime());

    final String area = ((int)Math.ceil(location.getEffectiveArea())) + "m²";

    final String explanation = address + " (" + period + "), " + area;
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
