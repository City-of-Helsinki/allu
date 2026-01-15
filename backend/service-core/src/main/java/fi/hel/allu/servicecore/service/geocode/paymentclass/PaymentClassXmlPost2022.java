package fi.hel.allu.servicecore.service.geocode.paymentclass;


import fi.hel.allu.servicecore.service.geocode.featuremember.FeatureClassMember;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "FeatureCollection", namespace = "http://www.opengis.net/wfs")
public class PaymentClassXmlPost2022 implements PaymentClassXml {

  @XmlElement(name = "featureMember", namespace = "http://www.opengis.net/gml")
  public List<FeatureClassMember> featureMember;

  @Override
  public List<FeatureClassMember> getFeatureMemeber() {
    return featureMember;
  }
}
