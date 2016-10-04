package fi.hel.allu.model.dao;

import static org.geolatte.geom.builder.DSL.c;
import static org.geolatte.geom.builder.DSL.geometrycollection;
import static org.geolatte.geom.builder.DSL.polygon;
import static org.geolatte.geom.builder.DSL.ring;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.model.testUtils.TestCommon;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ModelApplication.class)
@WebAppConfiguration
public class LocationDaoTest {

  @Autowired
  LocationDao locationDao;

  @Autowired
  TestCommon testCommon;

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

}
