package fi.hel.allu.servicecore.service.geocode.paymentclass;

import java.util.List;

public class PolygonCoordinates {

  public String outerBoundaryCoordinates;

  public List<String> innerBoundaryCoordinates;

  public PolygonCoordinates(String outerBoundaryCoordinates, List<String> innerBoundaryCoordinates) {
    this.outerBoundaryCoordinates = outerBoundaryCoordinates;
    this. innerBoundaryCoordinates = innerBoundaryCoordinates;
  }

  public String getOuterBoundaryCoordinates() {
    return outerBoundaryCoordinates;
  }

  public List<String> getInnerBoundaryCoordinates() {
    return innerBoundaryCoordinates;
  }
}
