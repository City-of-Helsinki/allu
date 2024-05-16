package fi.hel.allu.model.util;

import fi.hel.allu.model.domain.SupervisionTaskLocation;
import org.geolatte.geom.Geometry;

import java.util.List;

public class SupervisionLocationMap {

    SupervisionTaskLocation supervisionTaskLocation;
    List<Geometry> geometries;

    public SupervisionLocationMap(SupervisionTaskLocation supervisionTaskLocation, List<Geometry> geometyr) {
        this.supervisionTaskLocation = supervisionTaskLocation;
        geometries = geometyr;
    }

    public SupervisionTaskLocation getSupervisionTaskLocation() {
        return supervisionTaskLocation;
    }

    public void setSupervisionTaskLocation(SupervisionTaskLocation supervisionTaskLocation) {
        this.supervisionTaskLocation = supervisionTaskLocation;
    }

    public List<Geometry> getGeometries() {
        return geometries;
    }

    public void setGeometries(List<Geometry> geometries) {
        this.geometries = geometries;
    }
}