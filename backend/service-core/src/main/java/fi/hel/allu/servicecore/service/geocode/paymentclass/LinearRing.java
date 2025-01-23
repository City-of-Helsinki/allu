package fi.hel.allu.servicecore.service.geocode.paymentclass;

import javax.xml.bind.annotation.XmlElement;

public class LinearRing {

  @XmlElement(name = "coordinates", namespace = "http://www.opengis.net/gml")
  public String coordinates;

  public String getCoordinates() { return coordinates; }
}
