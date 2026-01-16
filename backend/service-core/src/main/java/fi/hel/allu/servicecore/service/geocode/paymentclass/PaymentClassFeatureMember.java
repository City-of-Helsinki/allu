package fi.hel.allu.servicecore.service.geocode.paymentclass;

import fi.hel.allu.servicecore.service.geocode.paymentclass.PaymentClass;
import fi.hel.allu.servicecore.service.geocode.VariablesUtils;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

public class PaymentClassFeatureMember {

  @XmlElements({
    @XmlElement(name = "Katutoiden_maksuluokat", namespace = VariablesUtils.HELSINKI_NAMESPACE),
    @XmlElement(name = "Katutoiden_maksuluokat_2022", namespace = VariablesUtils.HELSINKI_NAMESPACE),
    @XmlElement(name = "Katutoiden_maksuluokat_2025", namespace = VariablesUtils.HELSINKI_NAMESPACE)
  })
  public PaymentClass paymentClass;

  public PaymentClass getPaymentClass() {
    return paymentClass;
  }

  @Override
  public String toString() {
    return "PaymentClassFeatureMember{" +
      "paymentClass=" + paymentClass +
      '}';
  }
}
