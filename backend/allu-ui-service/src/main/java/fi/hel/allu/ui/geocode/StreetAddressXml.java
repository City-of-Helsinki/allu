package fi.hel.allu.ui.geocode;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * XML Mapping for WFSFeatureCollection consisting of street addresses.
 */
@XmlRootElement(name = "FeatureCollection", namespace = "http://www.opengis.net/wfs")
public class StreetAddressXml {
  @XmlElement(name = "featureMember", namespace = "http://www.opengis.net/gml")
  public List<FeatureMember> featureMember;

  public static class FeatureMember {
    @XmlElement(name = "Helsinki_osoiteluettelo", namespace = "http://www.hel.fi/hel")
    public HelsinkiOsoiteLuettelo geocodedAddress;
  }

  public static class HelsinkiOsoiteLuettelo {
    @XmlElement(name = "katunimi", namespace = "http://www.hel.fi/hel")
    public String streetName;
    @XmlElement(name = "osoitenumero", namespace = "http://www.hel.fi/hel")
    public int streetNumber;
    // TODO: rename y and x to lat and long to make names more describing
    @XmlElement(name = "n", namespace = "http://www.hel.fi/hel")
    public double y;
    @XmlElement(name = "e", namespace = "http://www.hel.fi/hel")
    public double x;
    @XmlElement(name = "kaupunki", namespace = "http://www.hel.fi/hel")
    public String city;
    @XmlElement(name = "postinumero", namespace = "http://www.hel.fi/hel")
    public String postalCode;
  }
}
