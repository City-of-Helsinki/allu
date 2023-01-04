package fi.hel.allu.model.domain;

public class LocationFlids implements LocationIdI {

    private Integer id;
    private Integer locationId;
    private Integer fixedLocationId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Integer getFixedLocationId() {
        return fixedLocationId;
    }

    public void setFixedLocationId(Integer fixedLocationId) {
        this.fixedLocationId = fixedLocationId;
    }
}