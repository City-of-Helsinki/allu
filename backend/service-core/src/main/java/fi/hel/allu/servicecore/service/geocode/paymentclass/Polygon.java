package fi.hel.allu.servicecore.service.geocode.paymentclass;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

public class Polygon {

  @XmlElement(name = "outerBoundaryIs", namespace = "http://www.opengis.net/gml")
  public OuterBoundary outerBoundary;

  @XmlElement(name = "innerBoundaryIs", namespace = "http://www.opengis.net/gml")
  public List<InnerBoundary> innerBoundaries;

  public PolygonCoordinates getCoordinates() {
    List<String> innerBoundaryCoordinates = new ArrayList<>();
    if (innerBoundaries != null)
      for (InnerBoundary innerBoundary : innerBoundaries)
	innerBoundaryCoordinates.add(innerBoundary.getCoordinates());
    return new PolygonCoordinates(outerBoundary.getCoordinates(), innerBoundaryCoordinates);
  }
}
