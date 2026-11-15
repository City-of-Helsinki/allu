package fi.hel.allu.servicecore.service.geocode.paymentclass;

import jakarta.xml.bind.annotation.XmlElement;

public class LinearRing {

  @XmlElement(name = "coordinates", namespace = "http://www.opengis.net/gml")
  public String coordinates;

  public String getCoordinates() { return coordinates; }
}
