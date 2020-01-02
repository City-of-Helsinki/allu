package fi.hel.allu.external.domain;

import org.geolatte.geom.Geometry;

public interface HasGeometry {
  Geometry getGeometry();
}
