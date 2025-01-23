package fi.hel.allu.servicecore.service.geocode.paymentclass;

import javax.xml.bind.annotation.XmlElement;

public class Polygon {

  @XmlElement(name = "outerBoundaryIs", namespace = "http://www.opengis.net/gml")
  public OuterBoundary outerBoundary;

  public String getCoordinates() { return outerBoundary.getCoordinates(); }
}
