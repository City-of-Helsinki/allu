package fi.hel.allu.model.domain;

import org.geolatte.geom.Geometry;

public class LocationGeometry implements LocationIdI {

    private Integer Id;
    private Geometry geometry;
    private Integer locationId;

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }
}