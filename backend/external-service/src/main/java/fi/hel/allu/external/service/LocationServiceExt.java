package fi.hel.allu.external.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.hel.allu.external.domain.LocationExt;
import fi.hel.allu.external.domain.PostalAddressExt;
import fi.hel.allu.external.domain.StreetAddressExt;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.servicecore.service.LocationService;

@Service
public class LocationServiceExt {

  @Autowired
  private LocationService locationService;

  public LocationExt findByApplicationId(Integer applicationId, Integer srId) {
    Location location = locationService.getSingleLocationByApplicationId(applicationId, srId);
    return mapToExt(location);
  }

  private LocationExt mapToExt(Location location) {
    LocationExt result = new LocationExt();
    result.setAddress(getAddress(location));
    result.setArea(location.getEffectiveArea());
    result.setPaymentTariff(location.getEffectivePaymentTariff());
    result.setAddress(getAddress(location));
    result.setCityDistrict(getCityDistrict(location));
    result.setGeometry(location.getGeometry());
    result.setAdditionalInfo(location.getAdditionalInfo());
    result.setFixedLocationIds(location.getFixedLocationIds());
    return result;
  }

  private String getCityDistrict(Location location) {
    return Optional.ofNullable(location.getEffectiveCityDistrictId()).map(id -> getCityDistrictName(id)).orElse(null);
  }

  private String getCityDistrictName(Integer id) {
    return locationService.getCityDistrictName(id);
  }

  private static PostalAddressExt getAddress(Location location) {
    return Optional.ofNullable(location.getPostalAddress())
        .map(a -> new PostalAddressExt(new StreetAddressExt(a.getStreetAddress()), a.getCity(), a.getPostalCode()))
        .orElse(null);
  }

}
