package fi.hel.allu.servicecore.service.geocode.paymentclass;


import fi.hel.allu.servicecore.service.geocode.featuremember.FeatureClassMember;
import fi.hel.allu.servicecore.service.geocode.featuremember.FeatureNewClassMember;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.stream.Collectors;

@XmlRootElement(name = "FeatureCollection", namespace = "http://www.opengis.net/wfs")
public class PaymentNewClassXml implements PaymentClassXml {

  @XmlElement(name = "featureMember", namespace = "http://www.opengis.net/gml")
  public List<FeatureNewClassMember> featureMember;

  @Override
  public List<FeatureClassMember> getFeatureMemeber() {
    return featureMember.stream().map(FeatureClassMember.class::cast).collect(Collectors.toList());
  }
}
