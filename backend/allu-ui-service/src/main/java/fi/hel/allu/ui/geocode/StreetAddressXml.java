package fi.hel.allu.ui.geocode;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * XML Mapping for WFSFeatureCollection consisting of street addresses.
 */
@XmlRootElement(name = "FeatureCollection", namespace = "http://www.opengis.net/wfs")
public class StreetAddressXml {

  public static final String NAMESPACE_HEL_FI_OPEN = "https://www.hel.fi/avoindata";

  @XmlElement(name = "featureMember", namespace = "http://www.opengis.net/gml")
  public List<FeatureMember> featureMember;

  public static class FeatureMember {
    @XmlElement(name = "Helsinki_osoiteluettelo", namespace = NAMESPACE_HEL_FI_OPEN)
    public HelsinkiOsoiteLuettelo geocodedAddress;
  }

  public static class HelsinkiOsoiteLuettelo {
    @XmlElement(name = "katunimi", namespace = NAMESPACE_HEL_FI_OPEN)
    public String streetName;
    @XmlElement(name = "osoitenumero", namespace = NAMESPACE_HEL_FI_OPEN)
    public int streetNumber;
    @XmlElement(name = "osoitenumero_teksti", namespace = NAMESPACE_HEL_FI_OPEN)
    public String streetNumberText;
    // TODO: rename y and x to lat and long to make names more describing
    @XmlElement(name = "n", namespace = NAMESPACE_HEL_FI_OPEN)
    public double y;
    @XmlElement(name = "e", namespace = NAMESPACE_HEL_FI_OPEN)
    public double x;
    @XmlElement(name = "kaupunki", namespace = NAMESPACE_HEL_FI_OPEN)
    public String city;
    @XmlElement(name = "postinumero", namespace = NAMESPACE_HEL_FI_OPEN)
    public String postalCode;
  }
}
