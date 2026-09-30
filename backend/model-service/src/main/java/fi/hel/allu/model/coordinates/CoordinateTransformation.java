package fi.hel.allu.model.coordinates;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.SQLQueryFactory;
import org.geolatte.geom.Geometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

@Repository
public class CoordinateTransformation {

  @Autowired
  private SQLQueryFactory queryFactory;

  private static final Logger logger = LoggerFactory.getLogger(CoordinateTransformation.class);

  public Geometry transformCoordinates(Geometry geometry, Integer targetSrid) {
    if (geometry == null || geometry.isEmpty()) {
      logger.debug("Skipping coordinate transform: geometry is null or empty");
      return geometry;
    }

    if (targetSrid == null) {
      logger.debug("Skipping coordinate transform: targetReferenceSystemId is null");
      return geometry;
    }

    if (targetSrid.equals(geometry.getSRID())) {
      return geometry;
    }

    try {
      int sourceSrid = geometry.getSRID();
      boolean sourceIsDegrees = sourceSrid == 4326;

      for (int i = 0; i < geometry.getNumPoints(); i++) {
        double x = geometry.getPoints().getX(i);
        double y = geometry.getPoints().getY(i);

        // Jos SRID ei ole asteissa, mutta koordinaatit ovat pienet → ilmeinen virhe
        if (!sourceIsDegrees && Math.abs(x) <= 180 && Math.abs(y) <= 90) {
          logger.warn("Geometry seems to be in degrees, not metres — SRID={} → targetSRID={}", sourceSrid, targetSrid);
        }

        // Jos SRID on asteissa, mutta koordinaatit ylittävät maantieteelliset rajat
        if (sourceIsDegrees && (Math.abs(x) > 180 || Math.abs(y) > 90)) {
          logger.warn("Geometry seems to be in metres, not degrees — SRID={} → targetSRID={}", sourceSrid, targetSrid);
        }
      }

      Geometry transformedCoord = queryFactory
        .select(Expressions.simpleTemplate(Geometry.class, "st_transform({0}, {1})", geometry, targetSrid))
        .fetchFirst();
      // Spring jackson binds can't handle infinite mark when converting rest call between containers
      return Double.isFinite(transformedCoord.getPoints().getX(0)) ? transformedCoord : null;
    } catch (DataAccessException e) {
      logger.warn("Coordinate transformation failed for SRID {} → {}: {}",
        geometry.getSRID(), targetSrid, e.getMessage());
      return null;
    } catch (Exception e) {
      logger.error("Unexpected error during coordinate transformation", e);
      return null;
    }
  }
}
