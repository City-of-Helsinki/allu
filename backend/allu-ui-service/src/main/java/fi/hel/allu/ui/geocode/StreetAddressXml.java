package fi.hel.allu.ui.geocode;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * XML Mapping for WFSFeatureCollection consisting of street addresses.
 */
@XmlRootElement(name = "FeatureCollection", namespace = "http://www.opengis.net/wfs")
public class StreetAddressXml {

  public static final String NAMESPACE_HEL_FI = "https://www.hel.fi/hel";

  @XmlElement(name = "featureMember", namespace = "http://www.opengis.net/gml")
  public List<FeatureMember> featureMember;

  public static class FeatureMember {
    @XmlElement(name = "Helsinki_osoiteluettelo", namespace = NAMESPACE_HEL_FI)
    public HelsinkiOsoiteLuettelo geocodedAddress;
  }

  public static class HelsinkiOsoiteLuettelo {
    @XmlElement(name = "katunimi", namespace = NAMESPACE_HEL_FI)
    public String streetName;
    @XmlElement(name = "osoitenumero", namespace = NAMESPACE_HEL_FI)
    public int streetNumber;
    // TODO: rename y and x to lat and long to make names more describing
    @XmlElement(name = "n", namespace = NAMESPACE_HEL_FI)
    public double y;
    @XmlElement(name = "e", namespace = NAMESPACE_HEL_FI)
    public double x;
    @XmlElement(name = "kaupunki", namespace = NAMESPACE_HEL_FI)
    public String city;
    @XmlElement(name = "postinumero", namespace = NAMESPACE_HEL_FI)
    public String postalCode;
  }
}
