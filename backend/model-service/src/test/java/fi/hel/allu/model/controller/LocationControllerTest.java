package fi.hel.allu.model.controller;

import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.model.domain.PostalAddress;
import fi.hel.allu.model.domain.user.User;
import fi.hel.allu.model.testUtils.TestCommon;
import fi.hel.allu.model.testUtils.WebTestCommon;
import org.geolatte.geom.Geometry;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.geolatte.geom.builder.DSL.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class LocationControllerTest {

  Application application;
  User testUser;

  @Autowired
  private WebTestCommon wtc;

  @Autowired
  TestCommon testCommon;

  @Before
  public void setup() throws Exception {
    wtc.setup();
    testUser = testCommon.insertUser("testUser");
    application = testCommon.dummyAreaRentalApplication("Test Application", "Handlaaja");
    ResultActions resultActions = wtc.perform(post("/applications?userId=" + testUser.getId()), application).andExpect(status().isOk());
    application = wtc.parseObjectFromResult(resultActions, Application.class);

  }

  @Test
  public void addLocation() throws Exception {
    // Add location without id. Should succeed:
    addLocation("Kuoppakuja 2", "06660", "Hellsinki", makeGeometry(), null).andExpect(status().isOk());
  }

  @Test
  public void addLocations() throws Exception {
    Location location1 = createDummyLocation("Kuoppakuja 2", "06660", "Hellsinki", makeGeometry(), null);
    Location location2 = createDummyLocation("Kuoppakuja 2", "07770", "Hellsinki", makeGeometry(), null);
    List<Location> locations = addLocationsAndGetResult(Arrays.asList(location1, location2));
    Assert.assertEquals(2, locations.size());
    // check some random data
    Assert.assertTrue(locations.stream()
        .map(l -> l.getPostalAddress().getPostalCode())
        .collect(Collectors.toSet())
        .containsAll(Arrays.asList(location1.getPostalAddress().getPostalCode(), location2.getPostalAddress().getPostalCode())));
  }

  @Test
  public void addLocationWithId() throws Exception {
    // add location with id. Should not use the given id
    addLocation("Kuoppakuja 2", "06660", "Hellsinki", makeGeometry(), 999999999)
        .andExpect(status().isOk()).andExpect(jsonPath("$.[0].id", not(999999999)));
  }

  @Test
  public void getLocation() throws Exception {
    // Setup: add location
    Location result = addLocationAndGetResult("Kuoppakuja 2", "06660", "Hellsinki", makeGeometry(), null);

    // Now check Kuoppakuja 2 got there.
    wtc.perform(get(String.format("/locations/%d", result.getId()))).andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(result.getId()))).andExpect(jsonPath("$.postalAddress.streetAddress", is("Kuoppakuja 2")));
  }

  @Test
  public void getNonexistentLocation() throws Exception {
    wtc.perform(get("/locations/239")).andExpect(status().isNotFound());
  }

  @Test
  public void updateLocation() throws Exception {
    // Setup: add location
    Location result = addLocationAndGetResult("Kuoppakuja 2", "06660", "Hellsinki", makeGeometry(), null);

    Location newLocation = new Location();
    newLocation.setApplicationId(application.getId());
    newLocation.setPostalAddress(new PostalAddress("Ikuisen Vapun Aukio 3", null, "Hellsing"));
    newLocation.setId(result.getId());
    newLocation.setUnderpass(false);
    newLocation.setStartTime(ZonedDateTime.now());
    newLocation.setEndTime(ZonedDateTime.now());
    wtc.perform(put(String.format("/locations/application/%d?userId=%d", application.getId(), testUser.getId())),
        Collections.singletonList(newLocation)).andExpect(status().isOk())
        .andExpect(jsonPath("$.[0].id", is(result.getId()))).andExpect(jsonPath("$.[0].postalAddress.city", is("Hellsing")));
  }

  @Test
  public void updateLocations() throws Exception {
    Location location1 = createDummyLocation("Kuoppakuja 2", "06660", "Hellsinki", makeGeometry(), null);
    Location location2 = createDummyLocation("Kuoppakuja 2", "07770", "Hellsinki", makeGeometry(), null);
    List<Location> locations = addLocationsAndGetResult(Arrays.asList(location1, location2));
    Location updatedLocation = locations.get(0);
    updatedLocation.getPostalAddress().setPostalCode("updated");
    Set<String> expectedPostalCodes = locations.stream().map(l -> l.getPostalAddress().getPostalCode()).collect(Collectors.toSet());
    wtc.perform(put(String.format("/locations/application/%d?userId=%d", application.getId(), testUser.getId())), locations)
        .andExpect(status().isOk());
    List<Location> updatedLocations = getLocationsByApplicationId(application.getId());
    Set<String> updatedPostalCodes = locations.stream().map(l -> l.getPostalAddress().getPostalCode()).collect(Collectors.toSet());
    Assert.assertTrue(updatedPostalCodes.containsAll(expectedPostalCodes));
  }

  private Geometry makeGeometry() {
    return geometrycollection(3879, ring(c(0, 0), c(0, 1), c(1, 1), c(1, 0), c(0, 0)));
  }

  // Helper to add location
  private ResultActions addLocation(String streetAddress, String postalCode, String city, Geometry geometry, Integer id)
      throws Exception {
    Location location = createDummyLocation(streetAddress, postalCode, city, geometry, id);
    return addLocations(Collections.singletonList(location));
  }

  private ResultActions addLocations(List<Location> locations)
      throws Exception {
    return wtc.perform(post("/locations?userId=" + testUser.getId()), locations);
  }

  private List<Location> addLocationsAndGetResult(List<Location> locations)
    throws Exception {
    return Arrays.asList(wtc.parseObjectFromResult(addLocations(locations), Location[].class));
  }

  private Location createDummyLocation(String streetAddress, String postalCode, String city, Geometry geometry, Integer id) {
    Location location = new Location();
    location.setApplicationId(application.getId());
    location.setPostalAddress(new PostalAddress(streetAddress, postalCode, city));
    location.setGeometry(geometry);
    location.setId(id);
    location.setUnderpass(false);
    location.setStartTime(ZonedDateTime.now());
    location.setEndTime(ZonedDateTime.now());
    return location;
  }

  // Add location, read response as Location
  private Location addLocationAndGetResult(String streetAddress, String postalCode, String city, Geometry geometry, Integer id)
      throws Exception {
    ResultActions resultActions = addLocation(streetAddress, postalCode, city, geometry, id).andExpect(status().isOk());
    return wtc.parseObjectFromResult(resultActions, Location[].class)[0];
  }

  private List<Location> getLocationsByApplicationId(int applicationId)
    throws Exception {
    ResultActions resultActions = wtc.perform(get(String.format("/locations/application/%d", applicationId))).andExpect(status().isOk());
    return Arrays.asList(wtc.parseObjectFromResult(resultActions, Location[].class));
  }
}
