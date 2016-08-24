package fi.hel.allu.ui.geocode;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * XML Mapping for WFSFeatureCollection.
 */
@XmlRootElement(name = "FeatureCollection", namespace = "http://www.opengis.net/wfs")
public class WfsFeatureCollection {
  @XmlElement(name = "featureMember")
  public List<FeatureMember> featureMember;

  public static class FeatureMember {
    @XmlElement(name = "Helsinki_osoiteluettelo")
    public HelsinkiOsoiteLuettelo geocodedAddress;
  }

  public static class HelsinkiOsoiteLuettelo {
    @XmlElement(name = "katunimi")
    public String streetName;
    @XmlElement(name = "osoitenumero")
    public int streetNumber;
    @XmlElement(name = "n")
    public double y;
    @XmlElement(name = "e")
    public double x;
    @XmlElement(name = "kaupunki")
    public String city;
    @XmlElement(name = "postinumero")
    public String postalCode;
  }
}
