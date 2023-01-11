package fi.hel.allu.model.domain;

import org.geolatte.geom.Geometry;

public interface LocationInterface {
    Integer getLocationKey();
    PostalAddressInterface getPostalAddress();
    Integer getId();
    Geometry getGeometry();
    String getAdditionalInfo();
    Integer getCityDistrictIdOverride();
    Integer getCityDistrictId();
}