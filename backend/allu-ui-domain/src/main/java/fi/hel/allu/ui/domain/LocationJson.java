package fi.hel.allu.ui.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fi.hel.allu.model.domain.serialization.GeometryDeserializerProxy;
import fi.hel.allu.model.domain.serialization.GeometrySerializerProxy;
import org.geolatte.geom.Geometry;

import javax.validation.Valid;

/**
 * inFinnish: Hakemuksen sijainti
 */
public class LocationJson {
  private Integer id;
  @JsonSerialize(using = GeometrySerializerProxy.class)
  @JsonDeserialize(using = GeometryDeserializerProxy.class)
  private Geometry geometry;
  @Valid
  private PostalAddressJson postalAddress;

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

  public PostalAddressJson getPostalAddress() {
    return postalAddress;
  }

  public void setPostalAddress(PostalAddressJson postalAddress) {
    this.postalAddress = postalAddress;
  }
}
