package fi.hel.allu.servicecore.service.geocode.paymentclass;

import javax.xml.bind.annotation.XmlElement;

public class OuterBoundary {

  @XmlElement(name = "LinearRing", namespace = "http://www.opengis.net/gml")
  public LinearRing linearRing;

  public String getCoordinates() { return linearRing.getCoordinates(); }
}
