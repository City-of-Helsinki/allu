package fi.hel.allu.model.domain;

import org.geolatte.geom.Geometry;

public class SupervisionTaskLocationGeometry {

    private Integer id;
    private Geometry geometry;
    private Integer supervisionLocationId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public Integer getSupervisionLocationId() {
        return supervisionLocationId;
    }

    public void setSupervisionLocationId(Integer supervisionLocationId) {
        this.supervisionLocationId = supervisionLocationId;
    }
}