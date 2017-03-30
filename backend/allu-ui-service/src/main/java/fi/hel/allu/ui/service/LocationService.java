package fi.hel.allu.ui.service;

import fi.hel.allu.model.domain.*;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationService {
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(LocationService.class);

  private ApplicationProperties applicationProperties;

  private RestTemplate restTemplate;

  @Autowired
  public LocationService(ApplicationProperties applicationProperties, RestTemplate restTemplate) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
  }


  /**
   * Create new locations.
   *
   * @param locationJsons  Locations to be created.
   * @return Created locations.
   */
  public List<LocationJson> createLocations(int applicationId, List<LocationJson> locationJsons) {
    if (locationJsons != null) {
      locationJsons.stream().forEach(l -> l.setId(null));
      Location[] createdLocations = restTemplate.postForObject(applicationProperties.getModelServiceUrl(
          ApplicationProperties.PATH_MODEL_LOCATION_CREATE), createLocationModel(applicationId, locationJsons),
          Location[].class);
      return mapToLocationJsons(createdLocations);
    } else {
      return Collections.emptyList();
    }
  }

  /**
   * Delete all locations from the given application.
   *
   * @param applicationId
   */
  // TODO: remove when locations are removed from the application class
  public void deleteApplicationLocation(int applicationId) {
    restTemplate.delete(applicationProperties.getDeleteLocationsByApplicationIdUrl(), applicationId);
  }

  /**
   * Update application's locations.
   *
   * @param applicationId
   * @param locations
   * @return Updated locations.
   */
  public List<LocationJson> updateApplicationLocations(int applicationId, List<LocationJson> locations) {
    HttpEntity<List<Location>> requestEntity = new HttpEntity<>(createLocationModel(applicationId, locations));
    ResponseEntity<Location[]> responseEntity = restTemplate.exchange(
        applicationProperties.getUpdateApplicationLocationsUrl(),
            HttpMethod.PUT,
            requestEntity,
        Location[].class, applicationId);
    return mapToLocationJsons(responseEntity.getBody());
  }

  public List<LocationJson> insert(int applicationId, List<LocationJson> locations) {
    return mapToLocationJsons(
        restTemplate.postForObject(
            applicationProperties.getLocationsCreateUrl(),
            createLocationModel(applicationId, locations),
            Location[].class));
  }

  public void delete(List<Integer> locations) {
    restTemplate.postForObject(applicationProperties.getLocationsDeleteUrl(), locations, Void.class);
  }

  /**
   * Find given location details.
   *
   * @param locationId location identifier that is used to find details
   * @return Location details or empty location object
   */
  public LocationJson findLocationById(int locationId) {
    ResponseEntity<Location> locationResult = restTemplate.getForEntity(applicationProperties
        .getModelServiceUrl(ApplicationProperties.PATH_MODEL_LOCATION_FIND_BY_ID), Location.class, locationId);
    return mapToLocationJson(locationResult.getBody());
  }

  /**
   * Find given location details by related application.
   *
   * @param applicationId   Id of the application whose locations should be returned.
   * @return List of related locations or empty list.
   */
  public List<LocationJson> findLocationsByApplication(int applicationId) {
    ResponseEntity<Location[]> locationResult = restTemplate.getForEntity(
        applicationProperties.getLocationsByApplicationIdUrl(), Location[].class, applicationId);
    return mapToLocationJsons(locationResult.getBody());
  }

  /**
   * Retrieve the list of defined fixed-locations
   * @return list of FixedLocation locations
   */
  public List<FixedLocationJson> getFixedLocationList() {
    ResponseEntity<FixedLocation[]> queryResult =
        restTemplate.getForEntity(applicationProperties.getFixedLocationUrl(), FixedLocation[].class);
    List<FixedLocationJson> resultList = Arrays.stream(queryResult.getBody()).map(fl -> mapToFixedLocationJson(fl))
        .collect(Collectors.toList());
    return resultList;
  }

  /**
   * Retrieve the list of defined city districts
   *
   * @return list of city districts
   */
  public List<CityDistrictInfoJson> getCityDistrictList() {
    ResponseEntity<CityDistrictInfo[]> queryResult = restTemplate
        .getForEntity(applicationProperties.getCityDistrictUrl(), CityDistrictInfo[].class);
    List<CityDistrictInfoJson> resultList = Arrays.stream(queryResult.getBody()).map(LocationService::mapToJson)
        .collect(Collectors.toList());
    return resultList;
  }

  /**
   * Retrieve the list of defined fixed locations areas
   *
   * @return list of fixed location areas
   */
  public List<FixedLocationAreaJson> getFixedLocationAreaList() {
    ResponseEntity<FixedLocationArea[]> queryResult = restTemplate
        .getForEntity(applicationProperties.getFixedLocationAreaUrl(), FixedLocationArea[].class);
    List<FixedLocationAreaJson> resultList = Arrays.stream(queryResult.getBody())
        .map(fla -> mapToFixedLocationAreaJson(fla)).collect(Collectors.toList());
    return resultList;
  }

  private List<Location> createLocationModel(int applicationId, List<LocationJson> locationJsons) {
    return locationJsons.stream().map(locationJson -> createLocationModel(applicationId, locationJson)).collect(Collectors.toList());
  }

  private Location createLocationModel(int applicationId, LocationJson locationJson) {
    if (locationJson == null) { throw new NullPointerException("LocationJson should not be null"); }
    Location location = new Location();
    location.setId(locationJson.getId());
    location.setLocationKey(locationJson.getLocationKey());
    location.setLocationVersion(locationJson.getLocationVersion());
    location.setStartTime(locationJson.getStartTime());
    location.setEndTime(locationJson.getEndTime());
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
    return location;
  }

  private List<LocationJson> mapToLocationJsons(Location[] locations) {
    return Arrays.stream(locations).map(l -> mapToLocationJson(l)).collect(Collectors.toList());
  }

  private LocationJson mapToLocationJson(Location location) {
    LocationJson locationJson = new LocationJson();
    locationJson.setId(location.getId());
    locationJson.setLocationKey(location.getLocationKey());
    locationJson.setLocationVersion(location.getLocationVersion());
    locationJson.setStartTime(location.getStartTime());
    locationJson.setEndTime(location.getEndTime());
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
    return locationJson;
  }

  private FixedLocationJson mapToFixedLocationJson(FixedLocation fixedLocation) {
    FixedLocationJson fixedLocationJson = new FixedLocationJson();
    fixedLocationJson.setId(fixedLocation.getId());
    fixedLocationJson.setArea(fixedLocation.getArea());
    fixedLocationJson.setSection(fixedLocation.getSection());
    fixedLocationJson.setApplicationKind(fixedLocation.getApplicationKind());
    fixedLocationJson.setGeometry(fixedLocation.getGeometry());

    return fixedLocationJson;
  }

  private FixedLocationAreaJson mapToFixedLocationAreaJson(FixedLocationArea fixedLocationArea) {
    FixedLocationAreaJson fixedLocationAreaJson = new FixedLocationAreaJson();
    fixedLocationAreaJson.setId(fixedLocationArea.getId());
    fixedLocationAreaJson.setName(fixedLocationArea.getName());
    fixedLocationAreaJson.setSections(fixedLocationArea.getSections().stream()
        .map(fls -> mapToFixedLocationSectionJson(fls)).collect(Collectors.toList()));
    return fixedLocationAreaJson;
  }

  private FixedLocationSectionJson mapToFixedLocationSectionJson(FixedLocationSection fixedLocationSection) {
    FixedLocationSectionJson fixedLocationSectionJson = new FixedLocationSectionJson();
    fixedLocationSectionJson.setId(fixedLocationSection.getId());
    fixedLocationSectionJson.setName(fixedLocationSection.getSection());
    fixedLocationSectionJson.setApplicationKind(fixedLocationSection.getApplicationKind());
    fixedLocationSectionJson.setGeometry(fixedLocationSection.getGeometry());
    return fixedLocationSectionJson;
  }

  private static CityDistrictInfoJson mapToJson(CityDistrictInfo cityDistrictInfo) {
    CityDistrictInfoJson result = new CityDistrictInfoJson();
    result.setId(cityDistrictInfo.getId());
    result.setDistrictId(cityDistrictInfo.getDistrictId());
    result.setName(cityDistrictInfo.getName());
    return result;
  }
}
