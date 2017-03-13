package fi.hel.allu.ui.service;

import fi.hel.allu.model.domain.CityDistrictInfo;
import fi.hel.allu.model.domain.FixedLocation;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.CityDistrictInfoJson;
import fi.hel.allu.ui.domain.FixedLocationJson;
import fi.hel.allu.ui.domain.LocationJson;
import fi.hel.allu.ui.domain.PostalAddressJson;

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
   * Create a new location. If the given location object is null, empty location
   * is created.
   *
   * @param locationJson
   *          Location that is going to be created
   * @return Created location
   */
  // TODO: remove when locations are removed from the application class
  public LocationJson createLocation(int applicationId, LocationJson locationJson) {
    if (locationJson == null) {
      locationJson = new LocationJson();
    }
    return callModelService(applicationId, locationJson);
  }

  // TODO: remove when locations are removed from the application class
  private LocationJson callModelService(int applicationId, LocationJson locationJson) {
    List<Location> location = Arrays.asList(
        restTemplate.postForObject(applicationProperties.getModelServiceUrl(
          ApplicationProperties.PATH_MODEL_LOCATION_CREATE), Collections.singletonList(createLocationModel(applicationId, locationJson)),
          Location[].class));
    return mapToLocationJson(location.get(0));
  }

  /**
   * Update the given location. Location is updated if the id is given.
   * Otherwise, new location is created.
   *
   * @param locationJson
   *          location that is going to be updated
   * @return locationJson result of the operation
   */
  // TODO: remove when locations are removed from the application class
  public LocationJson updateOrCreateLocation(int applicationId, LocationJson locationJson) {
    if (locationJson.getId() != null && locationJson.getId() > 0) {
      return update(applicationId, Collections.singletonList(locationJson)).get(0);
    } else {
      return createLocation(applicationId, locationJson);
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


  public List<LocationJson> update(int applicationId, List<LocationJson> locations) {
    HttpEntity<List<Location>> requestEntity = new HttpEntity<>(createLocationModel(applicationId, locations));
    ResponseEntity<Location[]> responseEntity = restTemplate.exchange(
            applicationProperties.getLocationsUpdateUrl(),
            HttpMethod.PUT,
            requestEntity,
            Location[].class);
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

  public List<CityDistrictInfoJson> getCityDistrictList() {
    ResponseEntity<CityDistrictInfo[]> queryResult = restTemplate
        .getForEntity(applicationProperties.getCityDistrictUrl(), CityDistrictInfo[].class);
    List<CityDistrictInfoJson> resultList = Arrays.stream(queryResult.getBody()).map(LocationService::mapToJson)
        .collect(Collectors.toList());
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
      location.setStreetAddress(locationJson.getPostalAddress().getStreetAddress());
      location.setPostalCode(locationJson.getPostalAddress().getPostalCode());
      location.setCity(locationJson.getPostalAddress().getCity());
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
    postalAddressJson.setCity(location.getCity());
    postalAddressJson.setPostalCode(location.getPostalCode());
    postalAddressJson.setStreetAddress(location.getStreetAddress());
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

  private static CityDistrictInfoJson mapToJson(CityDistrictInfo cityDistrictInfo) {
    CityDistrictInfoJson result = new CityDistrictInfoJson();
    result.setId(cityDistrictInfo.getId());
    result.setDistrictId(cityDistrictInfo.getDistrictId());
    result.setName(cityDistrictInfo.getName());
    return result;
  }
}
