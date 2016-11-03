package fi.hel.allu.model.dao;

import com.querydsl.sql.SQLQueryFactory;

import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.FixedLocation;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.model.testUtils.TestCommon;

import org.geolatte.geom.Geometry;
import org.geolatte.geom.GeometryCollection;
import org.geolatte.geom.builder.DSL.Polygon2DToken;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static fi.hel.allu.QFixedLocation.fixedLocation;
import static org.geolatte.geom.builder.DSL.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class LocationDaoTest {

  @Autowired
  LocationDao locationDao;

  @Autowired
  TestCommon testCommon;

  @Autowired
  private SQLQueryFactory queryFactory;

  @Before
  public void setUp() throws Exception {
    testCommon.deleteAllData();
  }

  // Test geometries: four 2x2 squares in a diagonal, three of them overlapping
  // each other and one separate from the others.
  private static final Polygon2DToken Sq_0_0 = polygon(ring(c(0, 0), c(0, 2), c(2, 2), c(2, 0), c(0, 0)));
  private static final Polygon2DToken Sq_1_1 = polygon(ring(c(1, 1), c(1, 3), c(3, 3), c(3, 1), c(1, 1)));
  private static final Polygon2DToken Sq_2_2 = polygon(ring(c(2, 2), c(2, 4), c(4, 4), c(4, 2), c(2, 2)));
  private static final Polygon2DToken Sq_5_5 = polygon(ring(c(5, 5), c(5, 7), c(7, 7), c(7, 5), c(5, 5)));

  @Test
  public void testArea() {
    // Single 4 m^2 square
    Geometry geoIn = geometrycollection(3879, Sq_0_0);
    Location locIn = new Location();
    locIn.setGeometry(geoIn);
    Location locOut = locationDao.insert(locIn);
    double area = locOut.getArea();
    // Area should be close to 4 m^2:
    double diff = Math.abs(area - 4.0);
    assertTrue(diff < 0.0001);
  }

  @Test
  public void testAreaNoGeometry() {
    Location locIn = new Location();
    Location locOut = locationDao.insert(locIn);
    double area = locOut.getArea();
    // Area should be close to 0 m^2:
    double diff = Math.abs(area - 0.0);
    assertTrue(diff < 0.0001);
  }

  @Test
  public void testAreaEmptyGeometry() {
    Geometry geoIn = geometrycollection(3879, new Polygon2DToken[0]);
    Location locIn = new Location();
    locIn.setGeometry(geoIn);
    Location locOut = locationDao.insert(locIn);
    double area = locOut.getArea();
    // Area should be close to 0 m^2:
    double diff = Math.abs(area - 0.0);
    assertTrue(diff < 0.0001);
  }

  @Test
  public void testCombineGeometries() {
    // three overlapping squares, 4 m^2 each.
    Geometry geoIn = geometrycollection(3879, Sq_0_0, Sq_1_1, Sq_2_2);
    Location locIn = new Location();
    locIn.setGeometry(geoIn);
    Location locOut = locationDao.insert(locIn);
    Geometry geoOut = locOut.getGeometry();
    assertNotNull(geoOut);
    GeometryCollection geoColl = (GeometryCollection) geoOut;
    assertNotNull(geoColl);
    // Geometries should now be combined into one
    assertEquals(1, geoColl.getNumGeometries());
    double area = locOut.getArea();
    // Area should be close to 10 m^2:
    double diff = Math.abs(area - 10.0);
    assertTrue(diff < 0.0001);
  }

  @Test
  public void testSeparateGeometries() {
    // three non-overlapping squares, 4 m^2 each.
    Geometry geoIn = geometrycollection(3879, Sq_0_0, Sq_2_2, Sq_5_5);
    Location locIn = new Location();
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
    // Area should be close to 10 m^2:
    double diff = Math.abs(area - 12.0);
    assertTrue(diff < 0.0001);
  }

  @Test
  public void testFixedLocationId() {
    // Setup: add fixed location with known ID
    final int FIXED_LOCATION_ID = 9876;
    long insertCount = queryFactory.insert(fixedLocation).set(fixedLocation.id, FIXED_LOCATION_ID)
        .set(fixedLocation.area, "Narinkka").set(fixedLocation.isActive, true)
        .set(fixedLocation.section, "lohko A").execute();
    assertEquals(1, insertCount);
    // Test: add location with fixedLocationId
    Location locIn = new Location();
    locIn.setFixedLocationId(FIXED_LOCATION_ID);
    Location locOut = locationDao.insert(locIn);
    assertEquals(FIXED_LOCATION_ID, locOut.getFixedLocationId().intValue());
  }

  @Test
  public void testGetFixedLocationList() {
    // Setup: add two active rows and one passive
    long insertCount = queryFactory.insert(fixedLocation).set(fixedLocation.area, "Kauppatori")
        .set(fixedLocation.section, "lohko A").set(fixedLocation.isActive, true).addBatch()
        .set(fixedLocation.area, "Senaatintori").set(fixedLocation.isActive, true).addBatch()
        .set(fixedLocation.area, "Kauppatori").set(fixedLocation.section, "lohko Q")
        .set(fixedLocation.isActive, false).addBatch().execute();
    assertEquals(3, insertCount);
    // Test: get list, should only one 2 items, and only one at Kauppatori
    List<FixedLocation> queryResult = locationDao.getFixedLocationList();
    assertEquals(2, queryResult.size());
    assertEquals(1, queryResult.stream().filter(sqs -> sqs.getArea().equals("Kauppatori")).count());
  }

}
