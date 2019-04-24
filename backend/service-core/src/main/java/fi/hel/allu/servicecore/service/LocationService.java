package fi.hel.allu.servicecore.service;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.geolatte.geom.Geometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import fi.hel.allu.common.domain.GeometryWrapper;
import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.domain.user.User;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.CityDistrictInfoJson;
import fi.hel.allu.servicecore.domain.FixedLocationJson;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.mapper.LocationMapper;
import fi.hel.allu.servicecore.mapper.UserMapper;

@Service
public class LocationService {
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(LocationService.class);

  private final ApplicationProperties applicationProperties;
  private final RestTemplate restTemplate;

  @Autowired
  public LocationService(ApplicationProperties applicationProperties,
      RestTemplate restTemplate) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
  }


  /**
   * Retrieve the list of defined fixed-locations
   * @return list of FixedLocation locations
   */
  public List<FixedLocationJson> getFixedLocationList() {
    return getFixedLocationList(null, null);
  }

  public List<FixedLocationJson> getFixedLocationList(ApplicationKind applicationKind, Integer srId) {
    URI uri = UriComponentsBuilder.fromHttpUrl(applicationProperties.getFixedLocationUrl())
        .queryParam("applicationkind", applicationKind)
        .queryParam("srid", srId)
        .buildAndExpand().toUri();
    ResponseEntity<FixedLocation[]> queryResult =
        restTemplate.getForEntity(uri, FixedLocation[].class);
    List<FixedLocationJson> resultList = Arrays.stream(queryResult.getBody()).map(fl -> LocationMapper.mapToFixedLocationJson(fl))
        .collect(Collectors.toList());
    return resultList;
  }

  public FixedLocationJson getFixedLocationById(Integer id, Integer srId) {
    URI uri = UriComponentsBuilder.fromHttpUrl(applicationProperties.getFixedLocationByIdUrl())
        .queryParam("srid", srId)
        .buildAndExpand(Collections.singletonMap("id", id)).toUri();
    FixedLocation fixedLocation = restTemplate.getForObject(uri, FixedLocation.class);
    return LocationMapper.mapToFixedLocationJson(fixedLocation);
  }

  public List<FixedLocationArea> getFixedLocationAreas() {
    ResponseEntity<FixedLocationArea[]> queryResult = restTemplate.getForEntity(
            applicationProperties.getFixedLocationAreasUrl(), FixedLocationArea[].class);
    return Arrays.asList(queryResult.getBody());
  }

  public List<Location> getLocationsByApplication(Integer applicationId) {
    ResponseEntity<Location[]> queryResult = restTemplate
        .getForEntity(applicationProperties.getLocationsByApplicationIdUrl(), Location[].class, applicationId);
    return Arrays.asList(queryResult.getBody());
  }

  public Location getSingleLocationByApplicationId(Integer applicationId, Integer srId) {
    URI uri = UriComponentsBuilder.fromHttpUrl(applicationProperties.getLocationsByApplicationIdUrl())
        .queryParam("srid", srId)
        .buildAndExpand(Collections.singletonMap("applicationId", applicationId)).toUri();
    Location[] result = restTemplate
        .getForEntity(uri.toString(), Location[].class, applicationId).getBody();
    return result.length > 0 ? result[0] : null;
  }

  /**
   * Retrieve the list of defined city districts
   *
   * @return list of city districts
   */
  public List<CityDistrictInfoJson> getCityDistrictList() {
    ResponseEntity<CityDistrictInfo[]> queryResult = restTemplate
        .getForEntity(applicationProperties.getCityDistrictsUrl(), CityDistrictInfo[].class);
    List<CityDistrictInfoJson> resultList = Arrays.stream(queryResult.getBody()).map(LocationMapper::mapToJson)
        .collect(Collectors.toList());
    return resultList;
  }

  public String getCityDistrictName(Integer id) {
    return restTemplate.getForObject(applicationProperties.getCityDistrictNameUrl(), String.class, id);
  }

  public CityDistrictInfoJson getCityDistrictById(Integer id) {
    CityDistrictInfo cityDistrict = restTemplate.getForObject(applicationProperties.getCityDistrictByIdUrl(),
        CityDistrictInfo.class, id);
    return LocationMapper.mapToJson(cityDistrict);
  }

  public boolean isValidGeometry(Geometry geometry) {
    return restTemplate.postForObject(applicationProperties.getIsValidGeometryUrl(), new GeometryWrapper(geometry), Boolean.class);
  }

  public UserJson findSupervisionTaskOwner(Application application) {
    Integer cityDistrict = application.getLocations().get(0).getEffectiveCityDistrictId();
    UserJson supervisionTaskOwner = null;
    if (cityDistrict != null) {
      try {
        supervisionTaskOwner = findSupervisionTaskOwner(application.getType(), cityDistrict);
      } catch (NoSuchEntityException e) {
        logger.warn("Didn't find supervisor for city district " + cityDistrict);
      }
    }
    return supervisionTaskOwner;

  }

  public UserJson findSupervisionTaskOwner(ApplicationType type, Integer cityDistrictId) {
    final ResponseEntity<User> queryResult = restTemplate
        .getForEntity(applicationProperties.getFindSupervisionTaskOwnerUrl(), User.class, cityDistrictId, type);
    return UserMapper.mapToUserJson(queryResult.getBody());
  }

  public Geometry transformCoordinates(Geometry geometry, int srId) {
    URI uri = UriComponentsBuilder.fromHttpUrl(applicationProperties.getTransformGeometryUrl())
        .queryParam("srid", srId)
        .buildAndExpand().toUri();
    return restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>(new GeometryWrapper(geometry)), GeometryWrapper.class).getBody().getGeometry();
  }
}
