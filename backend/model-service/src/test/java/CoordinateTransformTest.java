import static org.geolatte.geom.builder.DSL.c;
import static org.geolatte.geom.builder.DSL.geometrycollection;
import static org.geolatte.geom.builder.DSL.point;
import static org.junit.Assert.*;

import org.geolatte.geom.Geometry;
import org.geolatte.geom.builder.DSL;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.coordinates.CoordinateTransformation;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class CoordinateTransformTest {

  private static final Geometry geometry_3879 = geometrycollection(3879, point(c(25496382.7528706, 6673260.21794806)));
  private static final Geometry geometry_3067 = geometrycollection(3067, point(c(385421.6160279, 6672380.79455570)));
  private static final double ACCEPTED_DISTANCE = 0.001;

  @Autowired
  private CoordinateTransformation transformation;

  @Test
  public void shouldTransformFrom3879To3067() {
    Geometry result = transformation.transformCoordinates(geometry_3879, 3067);
    assertEquals(3067, result.getSRID());
    assertTrue(pointsCloseEnough(result, geometry_3067));
  }

  @Test
  public void shouldTransformFrom3067To3879() {
    Geometry result = transformation.transformCoordinates(geometry_3067, 3879);
    assertEquals(3879, result.getSRID());
    assertTrue(pointsCloseEnough(result, geometry_3879));
  }

  @Test
  public void shouldNotTransformIfSameSRID() {
    Geometry result = transformation.transformCoordinates(geometry_3067, 3067);
    assertEquals(geometry_3067, result);
    result = transformation.transformCoordinates(geometry_3879, 3879);
    assertEquals(geometry_3879, result);
  }

  private boolean pointsCloseEnough(Geometry result, Geometry geometry3067) {
    double distance = result.getPointN(0).distance(geometry3067.getPointN(0));
    System.out.println(distance);
    return distance < ACCEPTED_DISTANCE;
  }

  @Test
  public void shouldReturnNullForInvalidMeterGeometryWithDegreesSRID() {
    // Geometry näyttää metriluvuilta (x/y ~ 25 000 000, 6 700 000) mutta SRID on 4326 (astejärjestelmä)
    Geometry invalidGeometry = geometrycollection(4326, point(c(25496382.75, 6673260.21)));
    Geometry result = transformation.transformCoordinates(invalidGeometry, 3879);
    assertNull("Invalid meter geometry with degree SRID should return null", result);
  }

  @Test
  public void shouldReturnNullForInvalidDegreeGeometryWithMetreSRID() {
    // Geometry näyttää asteluvuilta (x/y ~ 24,60) mutta SRID on 3879 (metrijärjestelmä)
    Geometry invalidGeometry = geometrycollection(3879, point(c(24.94, 60.17)));
    Geometry result = transformation.transformCoordinates(invalidGeometry, 4326);
    assertNull("Invalid coordinate transformation should return null", result);
  }

  @Test
  public void shouldReturnValidGeometryEvenIfUnknownSRID() {
    Geometry unknownSridGeom = geometrycollection(2050, point(c(385421.6, 6672380.7)));
    Geometry result = transformation.transformCoordinates(unknownSridGeom, 3879);
    // Odotetaan, että transform onnistuu ja tulos ei ole null
    assertNotNull("Transform should not fail even with unknown SRID", result);
    assertEquals(3879, result.getSRID());
  }

  @Test
  public void shouldHandleNullGeometryGracefully() {
    Geometry result = transformation.transformCoordinates(null, 3879);
    assertNull("Null geometry should return null", result);
  }

  @Test
  public void shouldTransform4326To3879Normally() {
    Geometry geometry_4326 = geometrycollection(4326, point(c(24.941, 60.171)));
    Geometry result = transformation.transformCoordinates(geometry_4326, 3879);
    assertEquals(3879, result.getSRID());
  }

  @Test
  public void shouldTransformMultiPointGeometry() {
    Geometry multiPoint = geometrycollection(4326,
      point(c(24.941, 60.171)),
      point(c(24.950, 60.172))
    );
    Geometry result = transformation.transformCoordinates(multiPoint, 3879);
    assertEquals(3879, result.getSRID());
    assertEquals(2, result.getNumPoints());
  }

  @Test
  public void shouldTransformPolygonGeometry() {
    Geometry polygon = DSL.polygon(4326,
      DSL.ring(c(24.941,60.171), c(24.942,60.171), c(24.942,60.172), c(24.941,60.172), c(24.941,60.171))
    );
    Geometry result = transformation.transformCoordinates(polygon, 3879);
    assertEquals(3879, result.getSRID());
    assertTrue(result.getNumPoints() > 0);
  }

  @Test
  public void shouldReturnNullForMixedInvalidPoints() {
    Geometry mixed = geometrycollection(4326,
      point(c(24.941,60.171)),
      point(c(25496382.75, 6673260.21)) // ilmeisesti metriä
    );
    Geometry result = transformation.transformCoordinates(mixed, 3879);
    assertNull(result);
  }

  @Test
  public void shouldReturnNullForInfiniteCoordinates() {
    Geometry infGeom = geometrycollection(4326, point(c(Double.POSITIVE_INFINITY, 60.171)));
    Geometry result = transformation.transformCoordinates(infGeom, 3879);
    assertNull(result);
  }

  @Test
  public void shouldReturnNullForNaNCoordinates() {
    Geometry nanGeom = geometrycollection(4326, point(c(Double.NaN, 60.171)));
    Geometry result = transformation.transformCoordinates(nanGeom, 3879);
    assertNull(result);
  }

  @Test
  public void shouldTransformUnknownToUnknownSRID() {
    Geometry unknown = geometrycollection(2050, point(c(385421.6, 6672380.7)));
    Geometry result = transformation.transformCoordinates(unknown, 3067);
    assertNotNull(result);
    assertEquals(3067, result.getSRID());
  }
}
