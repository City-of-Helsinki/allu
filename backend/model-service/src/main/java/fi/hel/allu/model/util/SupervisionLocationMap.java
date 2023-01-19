package fi.hel.allu.model.util;

import fi.hel.allu.model.domain.SupervisionTaskLocation;
import org.geolatte.geom.Geometry;

import java.util.List;

public class SupervisionLocationMap {

    SupervisionTaskLocation supervisionTaskLocation;
    List<Geometry> Geometyr;

    public SupervisionLocationMap(SupervisionTaskLocation supervisionTaskLocation, List<Geometry> geometyr) {
        this.supervisionTaskLocation = supervisionTaskLocation;
        Geometyr = geometyr;
    }

    public SupervisionTaskLocation getSupervisionTaskLocation() {
        return supervisionTaskLocation;
    }

    public void setSupervisionTaskLocation(SupervisionTaskLocation supervisionTaskLocation) {
        this.supervisionTaskLocation = supervisionTaskLocation;
    }

    public List<Geometry> getGeometyr() {
        return Geometyr;
    }

    public void setGeometyr(List<Geometry> geometyr) {
        Geometyr = geometyr;
    }
}