package fi.hel.allu.servicecore.mapper;

import java.util.List;
import java.util.stream.Collectors;

import fi.hel.allu.model.domain.*;
import fi.hel.allu.servicecore.domain.*;

public class LocationMapper {

  public static List<Location> createLocationModel(Integer applicationId, List<LocationJson> locationJsons) {
    return locationJsons.stream().map(locationJson -> createLocationModel(applicationId, locationJson)).collect(Collectors.toList());
  }

  public static Location createLocationModel(Integer applicationId, LocationJson locationJson) {
    if (locationJson == null) { throw new NullPointerException("LocationJson should not be null"); }
    Location location = new Location();
    location.setId(locationJson.getId());
    location.setLocationKey(locationJson.getLocationKey());
    location.setLocationVersion(locationJson.getLocationVersion());
    location.setStartTime(locationJson.getStartTime());
    location.setEndTime(locationJson.getEndTime());
    location.setAdditionalInfo(locationJson.getAdditionalInfo());
    location.setApplicationId(applicationId);
    if (locationJson.getPostalAddress() != null) {
      location.setPostalAddress(new PostalAddress(
          locationJson.getPostalAddress().getStreetAddress(),
          locationJson.getPostalAddress().getPostalCode(),
          locationJson.getPostalAddress().getCity()));
    }
    location.setGeometry(locationJson.getGeometry());
    location.setArea(locationJson.getArea());
    location.setAreaOverride(locationJson.getAreaOverride());
    location.setFixedLocationIds(locationJson.getFixedLocationIds());
    location.setCityDistrictId(locationJson.getCityDistrictId());
    location.setCityDistrictIdOverride(locationJson.getCityDistrictIdOverride());
    location.setPaymentTariff(locationJson.getPaymentTariff());
    location.setPaymentTariffOverride(locationJson.getPaymentTariffOverride());
    location.setUnderpass(locationJson.getUnderpass());
    location.setCustomerStartTime(locationJson.getCustomerStartTime());
    location.setCustomerEndTime(locationJson.getCustomerEndTime());
    location.setCustomerReportingTime(locationJson.getCustomerReportingTime());
    return location;
  }

  public static List<LocationJson> mapToLocationJsons(List<Location> locations) {
    return locations.stream().map(l -> mapToLocationJson(l)).collect(Collectors.toList());
  }

  private static LocationJson mapToLocationJson(Location location) {
    LocationJson locationJson = new LocationJson();
    locationJson.setId(location.getId());
    locationJson.setLocationKey(location.getLocationKey());
    locationJson.setLocationVersion(location.getLocationVersion());
    locationJson.setStartTime(location.getStartTime());
    locationJson.setEndTime(location.getEndTime());
    locationJson.setAdditionalInfo(location.getAdditionalInfo());
    PostalAddressJson postalAddressJson = new PostalAddressJson();
    if (location.getPostalAddress() != null) {
      postalAddressJson.setStreetAddress(location.getPostalAddress().getStreetAddress());
      postalAddressJson.setPostalCode(location.getPostalAddress().getPostalCode());
      postalAddressJson.setCity(location.getPostalAddress().getCity());
    }
    locationJson.setPostalAddress(postalAddressJson);
    locationJson.setGeometry(location.getGeometry());
    locationJson.setArea(location.getArea());
    locationJson.setAreaOverride(location.getAreaOverride());
    locationJson.setFixedLocationIds(location.getFixedLocationIds());
    locationJson.setCityDistrictId(location.getCityDistrictId());
    locationJson.setCityDistrictIdOverride(location.getCityDistrictIdOverride());
    locationJson.setPaymentTariff(location.getPaymentTariff());
    locationJson.setPaymentTariffOverride(location.getPaymentTariffOverride());
    locationJson.setUnderpass(location.getUnderpass());
    locationJson.setCustomerStartTime(location.getCustomerStartTime());
    locationJson.setCustomerEndTime(location.getCustomerEndTime());
    locationJson.setCustomerReportingTime(location.getCustomerReportingTime());
    return locationJson;
  }

  public static FixedLocationJson mapToFixedLocationJson(FixedLocation fixedLocation) {
    FixedLocationJson fixedLocationJson = new FixedLocationJson();
    fixedLocationJson.setId(fixedLocation.getId());
    fixedLocationJson.setArea(fixedLocation.getArea());
    fixedLocationJson.setSection(fixedLocation.getSection());
    fixedLocationJson.setApplicationKind(fixedLocation.getApplicationKind());
    fixedLocationJson.setGeometry(fixedLocation.getGeometry());
    fixedLocationJson.setActive(fixedLocation.isActive());

    return fixedLocationJson;
  }

  public static CityDistrictInfoJson mapToJson(CityDistrictInfo cityDistrictInfo) {
    CityDistrictInfoJson result = new CityDistrictInfoJson();
    result.setId(cityDistrictInfo.getId());
    result.setDistrictId(cityDistrictInfo.getDistrictId());
    result.setName(cityDistrictInfo.getName());
    return result;
  }



}
