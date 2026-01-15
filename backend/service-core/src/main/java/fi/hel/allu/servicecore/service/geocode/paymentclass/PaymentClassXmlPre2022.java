package fi.hel.allu.servicecore.service.geocode.paymentclass;

import fi.hel.allu.servicecore.service.geocode.featuremember.FeatureClassMember;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * XML Mapping for WFSFeatureCollection consisting of Helsinki payment classes.
 */
@XmlRootElement(name = "FeatureCollection", namespace = "http://www.opengis.net/wfs")
public class PaymentClassXmlPre2022 implements PaymentClassXml {

  @XmlElement(name = "featureMember", namespace = "http://www.opengis.net/gml")
  public List<FeatureClassMember> featureMember;


  @Override
  public List<FeatureClassMember> getFeatureMemeber() {
    return featureMember;
  }
}
