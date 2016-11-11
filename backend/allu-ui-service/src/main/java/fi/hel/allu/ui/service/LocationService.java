package fi.hel.allu.ui.service;

import fi.hel.allu.model.domain.FixedLocation;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.FixedLocationJson;
import fi.hel.allu.ui.domain.LocationJson;
import fi.hel.allu.ui.domain.PostalAddressJson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
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
   * Create a new location. If the given location object is null or id is null, empty location is created.
   *
   * @param locationJson Location that is going to be created
   * @return Created location
   */
  public LocationJson createLocation(LocationJson locationJson) {
    if (locationJson != null && (locationJson.getId() == null || locationJson.getId() == 0)) {
      callModelService(locationJson);
    } else if (locationJson == null || locationJson.getId() == null || locationJson.getId() == 0) {
      locationJson = new LocationJson();
      callModelService(locationJson);
    }
    return locationJson;
  }

  private void callModelService(LocationJson locationJson) {
    Location location = restTemplate.postForObject(applicationProperties
            .getModelServiceUrl(ApplicationProperties.PATH_MODEL_LOCATION_CREATE), createLocationModel(locationJson),
        Location.class);
    mapLocationToJson(locationJson, location);
  }

  /**
   * Update the given location. Location is updated if the id is given.
   * Otherwise, new location is created.
   *
   * @param locationJson
   *          location that is going to be updated
   * @return locationJson result of the operation
   */
  public LocationJson updateOrCreateLocation(LocationJson locationJson) {
    if (locationJson.getId() != null && locationJson.getId() > 0) {
      restTemplate.put(applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_LOCATION_UPDATE), createLocationModel
          (locationJson), locationJson.getId().intValue());
      return locationJson;
    } else {
      return createLocation(locationJson);
    }
  }

  /**
   * Delete location from the given application.
   *
   * @param applicationId
   */
  public void deleteApplicationLocation(int applicationId) {
    restTemplate.delete(
        applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_APPLICATION_DELETE_LOCATION),
        applicationId);
  }

  /**
   * Find given location details.
   *
   * @param locationId location identifier that is used to find details
   * @return Location details or empty location object
   */
  public LocationJson findLocationById(int locationId) {
    LocationJson locationJson = new LocationJson();
    ResponseEntity<Location> locationResult = restTemplate.getForEntity(applicationProperties
        .getModelServiceUrl(ApplicationProperties.PATH_MODEL_LOCATION_FIND_BY_ID), Location.class, locationId);
    mapLocationToJson(locationJson, locationResult.getBody());
    return locationJson;
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

  private Location createLocationModel(LocationJson locationJson) {
    Location location = new Location();
    if (locationJson != null && locationJson.getId() != null) {
      location.setId(locationJson.getId());
    }
    if (locationJson != null && locationJson.getPostalAddress() != null) {
      location.setStreetAddress(locationJson.getPostalAddress().getStreetAddress());
      location.setPostalCode(locationJson.getPostalAddress().getPostalCode());
      location.setCity(locationJson.getPostalAddress().getCity());
    }
    location.setGeometry(locationJson.getGeometry());
    location.setArea(locationJson.getArea());
    location.setFixedLocationIds(locationJson.getFixedLocationIds());
    return location;
  }

  private void mapLocationToJson(LocationJson locationJson, Location location) {
    locationJson.setId(location.getId());
    PostalAddressJson postalAddressJson = new PostalAddressJson();
    postalAddressJson.setCity(location.getCity());
    postalAddressJson.setPostalCode(location.getPostalCode());
    postalAddressJson.setStreetAddress(location.getStreetAddress());
    locationJson.setPostalAddress(postalAddressJson);
    locationJson.setGeometry(location.getGeometry());
    locationJson.setArea(location.getArea());
    locationJson.setFixedLocationIds(location.getFixedLocationIds());
  }

  private FixedLocationJson mapToFixedLocationJson(FixedLocation fixedLocation) {
    FixedLocationJson fixedLocationJson = new FixedLocationJson();
    fixedLocationJson.setId(fixedLocation.getId());
    fixedLocationJson.setArea(fixedLocation.getArea());
    fixedLocationJson.setSection(fixedLocation.getSection());
    fixedLocationJson.setApplicationType(fixedLocation.getApplicationType());

    return fixedLocationJson;
  }
}
