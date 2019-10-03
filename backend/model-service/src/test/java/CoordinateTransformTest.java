import static org.geolatte.geom.builder.DSL.c;
import static org.geolatte.geom.builder.DSL.geometrycollection;
import static org.geolatte.geom.builder.DSL.point;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.geolatte.geom.Geometry;
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
    assertEquals(result, geometry_3067);
    result = transformation.transformCoordinates(geometry_3879, 3879);
    assertEquals(result, geometry_3879);
  }

  private boolean pointsCloseEnough(Geometry result, Geometry geometry3067) {
    double distance = result.getPointN(0).distance(geometry3067.getPointN(0));
    System.out.println(distance);
    return distance < ACCEPTED_DISTANCE;
  }

}
