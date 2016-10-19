package fi.hel.allu.model.controller;

import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.model.testUtils.WebTestCommon;

import org.geolatte.geom.Geometry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.geolatte.geom.builder.DSL.c;
import static org.geolatte.geom.builder.DSL.geometrycollection;
import static org.geolatte.geom.builder.DSL.ring;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class LocationControllerTest {

  @Autowired
  private WebTestCommon wtc;

  @Before
  public void setup() throws Exception {
    wtc.setup();
  }

  private Geometry makeGeometry() {
    return geometrycollection(3879, ring(c(0, 0), c(0, 1), c(1, 1), c(1, 0), c(0, 0)));
  }

  // Helper to add location
  private ResultActions addLocation(String streetAddress, String postalCode, String city, Geometry geometry, Integer id)
      throws Exception {
    Location location = new Location();
    location.setStreetAddress(streetAddress);
    location.setPostalCode(postalCode);
    location.setCity(city);
    location.setGeometry(geometry);
    location.setId(id);
    return wtc.perform(post("/locations"), location);
  }

  // Add location, read response as Location
  private Location addLocationAndGetResult(String streetAddress, String postalCode, String city, Geometry geometry,
      Integer id) throws Exception {
    ResultActions resultActions = addLocation(streetAddress, postalCode, city, geometry, id).andExpect(status().isOk());
    return wtc.parseObjectFromResult(resultActions, Location.class);
  }

  @Test
  public void addLocation() throws Exception {
    // Add location without id. Should succeed:
    addLocation("Kuoppakuja 2", "06660", "Hellsinki", makeGeometry(), null).andExpect(status().isOk());
  }

  @Test
  public void addLocationWithId() throws Exception {
    // add location with id. Should fail:
    addLocation("Kuoppakuja 2", "06660", "Hellsinki", makeGeometry(), 113).andExpect(status().isBadRequest());
  }

  @Test
  public void getLocation() throws Exception {
    // Setup: add location
    Location result = addLocationAndGetResult("Kuoppakuja 2", "06660", "Hellsinki", makeGeometry(), null);

    // Now check Kuoppakuja 2 got there.
    wtc.perform(get(String.format("/locations/%d", result.getId()))).andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(result.getId()))).andExpect(jsonPath("$.streetAddress", is("Kuoppakuja 2")));
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
    newLocation.setStreetAddress("Ikuisen Vapun Aukio 3");
    newLocation.setCity("Hellsing");
    newLocation.setId(999);
    wtc.perform(put(String.format("/locations/%d", result.getId())), newLocation).andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(result.getId()))).andExpect(jsonPath("$.city", is("Hellsing")));

  }

  @Test
  public void updateNonexistent() throws Exception {
    Location location = new Location();
    location.setStreetAddress("Ikuisen Vapun Aukio 3");
    location.setCity("Hellsing");
    location.setId(999);
    wtc.perform(put(String.format("/locations/27312")), location).andExpect(status().isNotFound());
  }
}
