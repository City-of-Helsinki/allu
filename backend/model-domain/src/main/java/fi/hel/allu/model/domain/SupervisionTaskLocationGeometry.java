package fi.hel.allu.model.domain;

import org.geolatte.geom.Geometry;

public class SupervisionTaskLocationGeometry {

    private Integer id;
    private Geometry geometry;
    private Integer supervisionLocationId;

    public SupervisionTaskLocationGeometry(Integer id, Geometry geometry, Integer supervisionLocationId) {
        this.id = id;
        this.geometry = geometry;
        this.supervisionLocationId = supervisionLocationId;
    }
}