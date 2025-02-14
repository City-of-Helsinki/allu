package fi.hel.allu.model.dao;

import com.querydsl.sql.SQLQueryFactory;
import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.FixedLocation;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.model.domain.PostalAddress;
import fi.hel.allu.model.testUtils.TestCommon;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.GeometryCollection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static fi.hel.allu.QFixedLocation.fixedLocation;
import static fi.hel.allu.QLocationArea.locationArea;
import static org.geolatte.geom.builder.DSL.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class LocationDaoTest {

  private Application application;

  @Autowired
  LocationDao locationDao;

  @Autowired
  ApplicationDao applicationDao;

  @Autowired
  PostalAddressDao postalAddressDao;

  @Autowired
  TestCommon testCommon;

  @Autowired
  private SQLQueryFactory queryFactory;

  @BeforeEach
  public void setUp() throws Exception {
    testCommon.deleteAllData();
    application = testCommon.dummyOutdoorApplication("test application", "käsittelijä");
    application = applicationDao.insert(application);
  }

  // Test geometries: four 2x2 squares in a diagonal, three of them overlapping
  // each other and one separate from the others.
  private static final Polygon2DToken Sq_0_0 = polygon(ring(c(0, 0), c(0, 2), c(2, 2), c(2, 0), c(0, 0)));
  private static final Polygon2DToken Sq_1_1 = polygon(ring(c(1, 1), c(1, 3), c(3, 3), c(3, 1), c(1, 1)));
  private static final Polygon2DToken Sq_2_2 = polygon(ring(c(2, 2), c(2, 4), c(4, 4), c(4, 2), c(2, 2)));
  private static final Polygon2DToken Sq_5_5 = polygon(ring(c(5, 5), c(5, 7), c(7, 7), c(7, 5), c(5, 5)));

  // Test geometries to test simplification:
  // three different size polygons to test simplification with different tolerances.
  private static final Polygon2DToken Pol_0_0 = polygon(ring(c(10, 0), c(0, 20), c(0, 40), c(20, 40),
    c(50, 20), c(60, 0), c(20, 20), c(10, 0)));
  private static final Polygon2DToken Pol_0_100 = polygon(ring(c(0, 100), c(0, 200), c(0, 700), c(300, 900),
    c(400, 700), c(600, 500), c(200, 100), c(0, 100)));

  @Test
  public void testLocationKeyGeneration() {
    Location locIn = newLocationWithDefaults();
    locIn.setApplicationId(application.getId());
    Location locOut1 = locationDao.insert(locIn);
    Location locOut2 = locationDao.insert(locIn);
    Location locOut3 = locationDao.insert(locIn);
    // insert 3 locations so that the greatest location key in database is 3
    assertEquals(1, (int) locOut1.getLocationKey());
    assertEquals(2, (int) locOut2.getLocationKey());
    assertEquals(3, (int) locOut3.getLocationKey());
    locationDao.deleteById(locOut2.getId());
    // expecting location key to increase, because the greatest number in database is 3
    Location locOut4 = locationDao.insert(locIn);
    assertEquals(4, (int) locOut4.getLocationKey());
    locationDao.deleteById(locOut4.getId());
    // expecting location key to not to increase, because the greatest number in database is still 3
    Location locOut5 = locationDao.insert(locIn);
    assertEquals(4, (int) locOut5.getLocationKey());
  }

  @Test
  public void testQueringByMultipleApplication() {
    Application application2 = testCommon.dummyOutdoorApplication("test application2", "pasi");
    application2 = applicationDao.insert(application2);
    Location locIn = newLocationWithDefaults();
    locIn.setApplicationId(application.getId());
    Location locOut1 = locationDao.insert(locIn);
    Location locOut2 = locationDao.insert(locIn);
    Location locOut3 = locationDao.insert(locIn);
    locIn.setApplicationId(application2.getId());
    Location locOut4 = locationDao.insert(locIn);
    Location locOut5 = locationDao.insert(locIn);
    List<Integer> applicationIds = Arrays.asList(application.getId(), application2.getId());
    List<Location> locations = locationDao.findByApplicationIds(applicationIds);
    assertEquals(5, locations.size());
  }

  @Test
  public void testMultipleLocationGeometriQuery(){
    Geometry geoIn = geometrycollection(3879, Sq_0_0);
    Geometry geoIn2 = geometrycollection(3879, Sq_5_5);
    Location locIn = newLocationWithDefaults();
    locIn.setGeometry(geoIn);
    locIn.setApplicationId(application.getId());
    Location locOut = locationDao.insert(locIn);
    Location location2 = newLocationWithDefaults();
    location2.setApplicationId(application.getId());
    location2.setGeometry(geoIn2);
    Location locOut2 = locationDao.insert(location2);
    List<Integer> applicationIds = Arrays.asList(application.getId());
    List<Location> locations = locationDao.findByApplicationIds(applicationIds);
    locations = locations.stream().sorted(Comparator.comparing(Location::getId)).collect(Collectors.toList());
    assertEquals(2, locations.size());
    assertEquals(geoIn.asText(), locations.get(0).getGeometry().asText());
    assertEquals(locations.get(1).getGeometry().asText(), geoIn2.asText());
  }

  @Test
  public void testLocationKeyUpdateWithNull() {
    Location locIn = newLocationWithDefaults();
    locIn.setApplicationId(application.getId());
    Location locOut1 = locationDao.insert(locIn);
    int loc1Key = locOut1.getLocationKey();
    int loc1Version = locOut1.getLocationVersion();
    locOut1.setLocationKey(null);
    locOut1.setLocationVersion(null);
    Location locOut2 = locationDao.updateApplicationLocations(application.getId(), Collections.singletonList(locOut1))
        .get(0);
    assertEquals(loc1Key, (int) locOut2.getLocationKey());
    assertEquals(loc1Version, (int) locOut2.getLocationVersion());
  }

  @Test
  public void testApplicationLocationsUpdateCreatesLocations() {
    Location locIn = newLocationWithDefaults();
    locIn.setApplicationId(application.getId());
    locIn.setGeometry(geometrycollection(3879, Sq_0_0));
    List<Location> newLocations = locationDao.updateApplicationLocations(application.getId(),
        Collections.singletonList(locIn));
    assertEquals(1, newLocations.size());
    assertEquals(locIn.getGeometry(), newLocations.get(0).getGeometry());
  }

  @Test
  public void testApplicationLocationAddRemoveUpdate() {
    // Set-up: add two locations to application
    Location locIn = newLocationWithDefaults();
    locIn.setApplicationId(application.getId());
    locIn.setGeometry(geometrycollection(3879, Sq_0_0));
    Location location1 = locationDao.insert(locIn);
    locIn.setGeometry(geometrycollection(3879, Sq_1_1));
    Location location2 = locationDao.insert(locIn);
    // Test: update one of the locations, remove one, and add a new one.
    location1.setGeometry(geometrycollection(3879, Sq_2_2));
    Location location3 = newLocationWithDefaults();
    location3.setApplicationId(application.getId());
    location3.setGeometry(geometrycollection(3879, Sq_5_5));
    List<Location> newLocations = locationDao.updateApplicationLocations(application.getId(),
        Arrays.asList(location1, location3));
    // location2 should be gone, location3 inserted
    assertEquals(2, newLocations.size());
    assertEquals(1, newLocations.stream().filter(l -> l.getId().equals(location1.getId())).count());
    assertEquals(0, newLocations.stream().filter(l -> l.getId().equals(location2.getId())).count());
    assertEquals(1, newLocations.stream().filter(l -> l.getGeometry().equals(location3.getGeometry())).count());
  }

  @Test
  public void testApplicationLocationsUpdateRemovesLocations() {
    // Set-up: add two locations to application
    Location locIn = newLocationWithDefaults();
    locIn.setApplicationId(application.getId());
    locIn.setGeometry(geometrycollection(3879, Sq_0_0));
    locationDao.insert(locIn);
    locIn.setGeometry(geometrycollection(3879, Sq_1_1));
    locationDao.insert(locIn);
    // Test: update with empty list, should remove all locations
    List<Location> newLocations = locationDao.updateApplicationLocations(application.getId(), Collections.emptyList());
    assertEquals(0, newLocations.size());
  }

  @Test
  public void testArea() {
    // Single 4 m^2 square
    Geometry geoIn = geometrycollection(3879, Sq_0_0);
    Location locIn = newLocationWithDefaults();
    locIn.setGeometry(geoIn);
    locIn.setApplicationId(application.getId());
    Location locOut = locationDao.insert(locIn);
    double area = locOut.getArea();
    assertEquals(locOut.getArea(), locOut.getEffectiveArea());
    // Area should be close to 4 m^2:
    double diff = Math.abs(area - 4.0);
    assertTrue(diff < 0.0001);
  }

  @Test
  public void testAreaNoGeometry() {
    Location locIn = newLocationWithDefaults();
    locIn.setApplicationId(application.getId());
    Location locOut = locationDao.insert(locIn);
    double area = locOut.getArea();
    assertEquals(locOut.getArea(), locOut.getEffectiveArea());
    // Area should be close to 0 m^2:
    double diff = Math.abs(area - 0.0);
    assertTrue(diff < 0.0001);
  }

  @Test
  public void testAreaOverride() {
    final double AREA_OVERRIDE = 123.23;
    Location locIn = newLocationWithDefaults();
    locIn.setApplicationId(application.getId());
    locIn.setAreaOverride(AREA_OVERRIDE);
    Location locOut = locationDao.insert(locIn);
    assertTrue(Math.abs(locOut.getAreaOverride() - AREA_OVERRIDE) < 0.0001);
  }

  @Test
  public void testAreaEmptyGeometry() {
    Geometry geoIn = geometrycollection(3879, new Polygon2DToken[0]);
    Location locIn = newLocationWithDefaults();
    locIn.setApplicationId(application.getId());
    locIn.setGeometry(geoIn);
    Location locOut = locationDao.insert(locIn);
    double area = locOut.getArea();
    assertEquals(locOut.getArea(), locOut.getEffectiveArea());
    // Area should be close to 0 m^2:
    double diff = Math.abs(area - 0.0);
    assertTrue(diff < 0.0001);
  }

  @Test
  public void testCombineGeometries() {
    // three overlapping squares, 4 m^2 each.
    Geometry geoIn = geometrycollection(3879, Sq_0_0, Sq_1_1, Sq_2_2);
    Location locIn = newLocationWithDefaults();
    locIn.setGeometry(geoIn);
    locIn.setApplicationId(application.getId());

    Location locOut = locationDao.insert(locIn);
    Geometry geoOut = locOut.getGeometry();
    assertNotNull(geoOut);
    GeometryCollection geoColl = (GeometryCollection) geoOut;
    assertNotNull(geoColl);
    // Geometries should now be combined into one
    assertEquals(1, geoColl.getNumGeometries());
    double area = locOut.getArea();
    assertEquals(locOut.getArea(), locOut.getEffectiveArea());
    // Area should be close to 10 m^2:
    double diff = Math.abs(area - 10.0);
    assertTrue(diff < 0.0001);
  }

  @Test
  public void testSeparateGeometries() {
    // three non-overlapping squares, 4 m^2 each.
    Geometry geoIn = geometrycollection(3879, Sq_0_0, Sq_2_2, Sq_5_5);
    Location locIn = newLocationWithDefaults();
    locIn.setApplicationId(application.getId());
    locIn.setGeometry(geoIn);
    Location locOut = locationDao.insert(locIn);
    Geometry geoOut = locOut.getGeometry();
    assertNotNull(geoOut);
    GeometryCollection geoColl = (GeometryCollection) geoOut;
    assertNotNull(geoColl);
    // Now the geometries should stay (at least partially) separate.
    // Since Sq_0_0 and Sq_2_2 share a vertex, they are probably combined,
    // but Sq_5_5 shoud stay separate in any case.
    assertTrue(geoColl.getNumGeometries() > 1);
    double area = locOut.getArea();
    assertEquals(locOut.getArea(), locOut.getEffectiveArea());
    // Area should be close to 10 m^2:
    double diff = Math.abs(area - 12.0);
    assertTrue(diff < 0.0001);
  }

  @Test
  public void testFixedLocationId() {
    // Setup: add fixed location with known ID
    int areaId = queryFactory.insert(locationArea).set(locationArea.name, "Turbofolkstraße")
        .executeWithKey(locationArea.id);
    final List<Integer> fixedLocationIds = Arrays.asList(9876, 3325, 2344);
    long insertCount = fixedLocationIds.stream()
        .mapToLong(flId -> queryFactory.insert(fixedLocation)
            .columns(fixedLocation.id, fixedLocation.areaId, fixedLocation.section, fixedLocation.applicationKind,
                fixedLocation.isActive)
            .values(flId, areaId, "lohko A" + flId, ApplicationKind.OUTDOOREVENT, true).execute())
        .sum();
    assertEquals(fixedLocationIds.size(), insertCount);
    // Test: add location with fixedLocationId
    Location locIn = newLocationWithDefaults();
    locIn.setApplicationId(application.getId());
    locIn.setFixedLocationIds(fixedLocationIds);
    Location locOut = locationDao.insert(locIn);
    // Check that all inserted IDs and none other are returned:
    TreeSet<Integer> flIdsIn = new TreeSet<>(fixedLocationIds);
    TreeSet<Integer> flIdsOut = new TreeSet<>(locOut.getFixedLocationIds());
    assertEquals(flIdsIn, flIdsOut);
  }

  @Test
  public void testGetFixedLocationList() {
    // Setup: add three active rows and one passive
    int kauppatoriId = queryFactory.insert(locationArea).set(locationArea.name, "Kauppatori")
        .executeWithKey(locationArea.id);
    int senaatintoriToriId = queryFactory.insert(locationArea).set(locationArea.name, "Senaatintori")
        .executeWithKey(locationArea.id);
    int kaivopuistoId = queryFactory.insert(locationArea).set(locationArea.name, "Kaivopuisto")
        .executeWithKey(locationArea.id);
    long insertCount =
        queryFactory.insert(fixedLocation)
            .set(fixedLocation.areaId, kauppatoriId).set(fixedLocation.section, "lohko A")
            .set(fixedLocation.applicationKind, ApplicationKind.OUTDOOREVENT).set(fixedLocation.isActive, true).addBatch()
            .set(fixedLocation.areaId, senaatintoriToriId)
            .set(fixedLocation.applicationKind, ApplicationKind.OUTDOOREVENT).set(fixedLocation.isActive, true).addBatch()
            .set(fixedLocation.areaId, kauppatoriId).set(fixedLocation.section, "lohko Q")
            .set(fixedLocation.applicationKind, ApplicationKind.OUTDOOREVENT).set(fixedLocation.isActive, false).addBatch()
            .set(fixedLocation.areaId, kaivopuistoId)
            .set(fixedLocation.applicationKind, ApplicationKind.SEASON_SALE).set(fixedLocation.isActive, true).addBatch()
            .execute();
    assertEquals(4, insertCount);
    // Test: get list, should only one 2 items, and only one at Kauppatori
    List<FixedLocation> queryResult = locationDao.getActiveFixedLocations(null, null);
    assertEquals(3, queryResult.size());
    assertEquals(1, queryResult.stream().filter(fl -> fl.getArea().equals("Kauppatori")).count());
  }

  // Polygon that's mostly in Herttoniemi:
  private static final Polygon2DToken herttoniemi_polygon = polygon(
      ring(c(25502404.097045037895441, 6675352.16425826959312), c(25502772.543876249343157, 6675370.854831204749644),
          c(25502767.204117525368929, 6675095.857257002033293), c(25502393.421108055859804, 6675101.196953509002924),
          c(25502404.097045037895441, 6675352.16425826959312)));

  @Test
  public void testFindDistrictOnInsert() {
    Location location = newLocationWithDefaults();
    location.setPostalAddress(new PostalAddress("Testiosoite 1", null, null));
    location.setGeometry(geometrycollection(3879, herttoniemi_polygon));
    location.setApplicationId(application.getId());
    Location inserted = locationDao.insert(location);

    // Check that the location now has a district ID:
    Integer cityDistrictId = inserted.getCityDistrictId();
    assertNotNull(cityDistrictId);

    // Make sure the district is Herttoniemi:
    String districtName = locationDao.getCityDistrictList().stream().filter(d -> d.getId() == cityDistrictId)
        .map(d -> d.getName()).findFirst().orElse("NOT FOUND");
    assertEquals("43 HERTTONIEMI", districtName);
  }

  @Test
  public void testFindDistrictOnUpdate() {

    Location location = newLocationWithDefaults();
    location.setPostalAddress(new PostalAddress("Testiosoite 1", null, null));
    location.setApplicationId(application.getId());
    Location inserted = locationDao.insert(location);
    assertNull(inserted.getCityDistrictId());

    inserted.setGeometry(geometrycollection(3879, herttoniemi_polygon));
    inserted = locationDao.updateApplicationLocations(application.getId(), Collections.singletonList(inserted)).get(0);

    // Check that the location now has a district ID:
    Integer cityDistrictId = inserted.getCityDistrictId();
    assertNotNull(cityDistrictId);
    assertEquals(cityDistrictId, inserted.getEffectiveCityDistrictId());

    // Make sure the district is Herttoniemi:
    String districtName = locationDao.getCityDistrictList().stream().filter(d -> d.getId() == cityDistrictId)
        .map(d -> d.getName()).findFirst().orElse("NOT FOUND");
    assertEquals("43 HERTTONIEMI", districtName);
  }

  @Test
  public void testLocationWithPostalAddress() {

    String streetAddress = "Testiosoite 1";
    String postalCode = "12312";
    String city = "testcity";
    Location location = newLocationWithDefaults();
    location.setPostalAddress(new PostalAddress(streetAddress, postalCode, city));
    location.setApplicationId(application.getId());
    Location inserted = locationDao.insert(location);
    assertEquals(streetAddress, inserted.getPostalAddress().getStreetAddress());
    assertEquals(postalCode, inserted.getPostalAddress().getPostalCode());
    assertEquals(city, inserted.getPostalAddress().getCity());

    // test updating postal address
    inserted.getPostalAddress().setStreetAddress("updated");
    Location updated = locationDao.updateApplicationLocations(application.getId(), Collections.singletonList(inserted))
        .get(0);
    assertEquals("updated", updated.getPostalAddress().getStreetAddress());
    assertEquals(inserted.getPostalAddress().getId(), updated.getPostalAddress().getId());

    // test deleting postal address
    updated.setPostalAddress(null);
    updated = locationDao.updateApplicationLocations(application.getId(), Collections.singletonList(updated)).get(0);
    assertNull(updated.getPostalAddress());

    assertFalse(postalAddressDao.findById(inserted.getPostalAddress().getId()).isPresent());
  }

  @Test
  public void testSimplifyTolerance3() {
    Geometry geometry = geometrycollection(3879, Pol_0_0);
    Geometry result = locationDao.simplifyGeometry(geometry, 3);
    assertEquals(geometry.getNumPoints(), result.getNumPoints(), "Should not simplify");
    assertNotEquals(0, result.getNumPoints(), "Simplify should always give at least 1 point, " +
            "given that it should not remove lines, only replace");

    geometry = geometrycollection(3879, Pol_0_100);
    result = locationDao.simplifyGeometry(geometry, 3);
    assertEquals(
      geometry.getNumPoints() - 1, result.getNumPoints(), "Should simplify 2 lines to 1 line");
    assertNotEquals(0, result.getNumPoints(),
                    "Simplify should always give at least 1 point, " +
                            "given that it should not remove lines, only replace");}

  @Test
  public void testSimplifyTolerance10() {
    Geometry geometry = geometrycollection(3879, Pol_0_0);
    Geometry result = locationDao.simplifyGeometry(geometry, 10);
    assertEquals(geometry.getNumPoints() - 2, result.getNumPoints(), "Should simplify 4 lines to 2 lines");
    assertNotEquals(0, result.getNumPoints(), "Simplify should always give at least 1 point, " +
            "given that it should not remove lines, only replace");

    geometry = geometrycollection(3879, Pol_0_100);
    result = locationDao.simplifyGeometry(geometry, 10);
    assertEquals(geometry.getNumPoints() - 1, result.getNumPoints(), "Should simplify 2 lines to 1 line");
    assertNotEquals(0, result.getNumPoints(), "Simplify should always give at least 1 point, " +
            "given that it should not remove lines, only replace");
  }

  @Test
  public void testSimplifyTolerance100() {
    Geometry geometry = geometrycollection(3879, Pol_0_0);
    Geometry result = locationDao.simplifyGeometry(geometry, 100);
    assertEquals(geometry.getNumPoints() - 4, result.getNumPoints(), "Should simplify 5 lines to 2 lines");
    assertNotEquals(0, result.getNumPoints(), "Simplify should always give at least 1 point, " +
            "given that it should not remove lines, only replace");

    geometry = geometrycollection(3879, Pol_0_100);
    result = locationDao.simplifyGeometry(geometry, 100);
    assertEquals(geometry.getNumPoints() - 2, result.getNumPoints(), "Should simplify 4 lines to 2 lines");
    assertNotEquals(0, result.getNumPoints(), "Simplify should always give at least 1 point, " +
            "given that it should not remove lines, only replace");
  }

  @Test
  public void testRemoveAdditionalInfoForApplication() {
    List<String> infos = List.of("XXX", "YYY", "ZZZ");
    List<Integer> appIds = new ArrayList<>();
    for (int i = 0; i < infos.size(); i++) {
      Application app = testCommon.dummyCableReportApplication("Application" + i, "Owner" + i);
      int appId = applicationDao.insert(app).getId();
      Location loc = new Location();
      loc.setGeometry(geometrycollection(3879, Pol_0_0));
      loc.setAdditionalInfo(infos.get(i));
      loc.setStartTime(ZonedDateTime.of(2025, 6, 1, 12, 0, 0, 0, TimeUtil.HelsinkiZoneId));
      loc.setEndTime(ZonedDateTime.of(2025, 6, 5, 12, 0, 0, 0, TimeUtil.HelsinkiZoneId));
      loc.setPaymentTariffOverride("3");
      loc.setApplicationId(appId);
      locationDao.insert(loc);
      appIds.add(appId);
    }

    locationDao.removeAdditionalInfoForApplications(List.of(appIds.get(0), appIds.get(1)));

    assertEquals("", applicationDao.findById(appIds.get(0)).getLocations().get(0).getAdditionalInfo());
    assertEquals("", applicationDao.findById(appIds.get(1)).getLocations().get(0).getAdditionalInfo());
    assertEquals("ZZZ", applicationDao.findById(appIds.get(2)).getLocations().get(0).getAdditionalInfo());
  }

  private Location newLocationWithDefaults() {
    Location location = new Location();
    location.setUnderpass(false);
    location.setStartTime(ZonedDateTime.now());
    location.setEndTime(ZonedDateTime.now());
    return location;
  }
}
