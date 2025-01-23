package fi.hel.allu.servicecore.service.geocode.paymentclass;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;
import java.util.stream.Collectors;

public class MultiPolygon {

  @XmlElement(name = "polygonMember", namespace = "http://www.opengis.net/gml")
  public List<PolygonMember> polygonMembers;

  public List<String> getCoordinates() {
    return polygonMembers.stream().map(PolygonMember::getCoordinates).collect(Collectors.toList());
  }
}
