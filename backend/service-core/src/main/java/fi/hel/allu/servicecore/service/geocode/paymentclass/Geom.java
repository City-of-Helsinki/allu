package fi.hel.allu.servicecore.service.geocode.paymentclass;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class Geom {

  @XmlElement(name = "MultiPolygon", namespace = "http://www.opengis.net/gml")
  public MultiPolygon multiPolygon;

  public List<PolygonCoordinates> getCoordinates() { return multiPolygon.getCoordinates();}
}
