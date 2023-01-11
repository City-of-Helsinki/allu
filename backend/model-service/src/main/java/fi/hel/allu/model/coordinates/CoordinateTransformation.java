package fi.hel.allu.model.coordinates;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.SQLQueryFactory;
import org.geolatte.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CoordinateTransformation {

  @Autowired
  private SQLQueryFactory queryFactory;

  public Geometry transformCoordinates(Geometry geometry, Integer targetReferenceSystemId) {
    if (geometry == null || geometry.isEmpty() || targetReferenceSystemId == null || targetReferenceSystemId.intValue() == geometry.getSRID()) {
      return geometry;
    }
    Geometry transformedCoord = queryFactory
            .select(Expressions.simpleTemplate(Geometry.class, "st_transform({0}, {1})", geometry,
                                               targetReferenceSystemId))
            .fetchFirst();
    // Spring jackson binds can't handle infinite mark when converting rest call between containers
    return Double.isFinite(transformedCoord.getPoints().getX(0)) ? transformedCoord : null;
  }
}