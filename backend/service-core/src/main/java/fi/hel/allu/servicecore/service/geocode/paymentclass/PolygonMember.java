package fi.hel.allu.servicecore.service.geocode.paymentclass;

import javax.xml.bind.annotation.XmlElement;

public class PolygonMember {

  @XmlElement(name = "Polygon", namespace = "http://www.opengis.net/gml")
  public Polygon polygon;

  public PolygonCoordinates getCoordinates() { return polygon.getCoordinates(); }
}
