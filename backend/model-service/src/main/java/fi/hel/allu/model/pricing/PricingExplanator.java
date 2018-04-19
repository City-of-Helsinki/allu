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
import java.util.Optional;

/**
 * Helper class for generating pricing explanation lines based on location.
 */
public class PricingExplanator {
  private final LocationDao locationDao;

  @Autowired
  public PricingExplanator(LocationDao locationDao) {
    this.locationDao = locationDao;
  }

  public List<String> getExplanation(Application application) {
    if (application.getId() == null) {
      return Collections.emptyList();
    }

    List<Location> locations = locationDao.findByApplication(application.getId());
    if (locations.size() > 1) {
      throw new RuntimeException("Only one location is supported in PricingExplanationService");
    } else if (locations.isEmpty()) {
      return Collections.emptyList();
    }
    return getExplanation(application, locations.get(0));
  }

  private List<String> getExplanation(Application application, Location location) {
    final List<FixedLocation> fixedLocations = new ArrayList<>();
    location.getFixedLocationIds().forEach((id) -> locationDao.findFixedLocation(id).map(fl -> fixedLocations.add(fl)));
    final String fixedLocation = Printable.forFixedLocations(fixedLocations);
    final String locationAddress = Printable.forPostalAddress(location.getPostalAddress());
    final String address = Optional.ofNullable(fixedLocation).orElse(locationAddress);

    final String period = Printable.forDayPeriod(application.getStartTime(), application.getEndTime());

    final String area = ((int)Math.ceil(location.getEffectiveArea())) + "m²";

    final List<String> explanation = new ArrayList<>();
    explanation.add(address + " (" + period + "), " + area);
    return explanation;
  }
}
