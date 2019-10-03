package fi.hel.allu.model.coordinates;

import org.geolatte.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.SQLQueryFactory;

@Repository
public class CoordinateTransformation {

  @Autowired
  private SQLQueryFactory queryFactory;

  public Geometry transformCoordinates(Geometry geometry, Integer targetReferenceSystemId) {
    if (geometry == null || geometry.isEmpty() || targetReferenceSystemId == null || targetReferenceSystemId.intValue() == geometry.getSRID()) {
      return geometry;
    }
    return queryFactory
        .select(Expressions.simpleTemplate(Geometry.class, "st_transform({0}, {1})", geometry, targetReferenceSystemId))
        .fetchFirst();
  }
}
