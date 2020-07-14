package fi.hel.allu.model.coordinates;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.SQLQueryFactory;
import org.geolatte.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class GeometrySimplification {

  @Autowired
  private SQLQueryFactory queryFactory;

  public Geometry simplifyGeometry(Geometry geometry, Integer tolerance) {
    if (geometry == null || geometry.isEmpty() || tolerance == null || tolerance == 0) {
      return geometry;
    }
    return queryFactory
        .select(Expressions.simpleTemplate(Geometry.class, "st_simplify({0}, {1}, true)", geometry, tolerance))
        .fetchFirst();
  }
}
