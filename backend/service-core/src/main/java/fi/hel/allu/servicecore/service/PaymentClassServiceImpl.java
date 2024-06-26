package fi.hel.allu.servicecore.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.service.geocode.featuremember.FeatureClassMember;
import fi.hel.allu.servicecore.service.geocode.paymentclass.PaymentClassXml;
import fi.hel.allu.servicecore.service.geocode.paymentclass.PaymentNewClassXml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import fi.hel.allu.common.wfs.WfsUtil;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.LocationJson;
import fi.hel.allu.servicecore.service.geocode.paymentclass.PaymentOldClassXml;
import fi.hel.allu.servicecore.util.AsyncWfsRestTemplate;

@Profile("!DEV")
@Service
public class PaymentClassServiceImpl extends AbstractWfsPaymentDataService implements PaymentClassService {


  private static final String FEATURE_TYPE_NAME = "Katutoiden_maksuluokat";
  private static final String FEATURE_PROPERTY_NAME = "maksuluokka";

  @Autowired
  public PaymentClassServiceImpl(ApplicationProperties applicationProperties, AsyncWfsRestTemplate restTemplate) {
    super(applicationProperties, restTemplate);
  }

  @Override
  public String getPaymentClass(LocationJson location, ApplicationJson applicationJson) {
    return executeWfsRequest(location, applicationJson);

  }


  @Override
  protected String parseResult(List<String> responses, ApplicationJson applicationJson) {
    String paymentClass = UNDEFINED;
    for (String response : responses) {
      final PaymentClassXml paymentOldClassXml = getPaymentClass(response, applicationJson);
      final List<FeatureClassMember> paymentClasses = paymentOldClassXml.getFeatureMemeber().stream()
          .sorted(Comparator.comparing(f -> f.getMaksuluokka().getPayment()))
          .collect(Collectors.toList());
      if (!paymentClasses.isEmpty()) {
        final String pc = paymentClasses.get(0).getMaksuluokka().getPayment();
        if (pc.compareTo(paymentClass) < 0) {
          paymentClass = pc;
        }
      }
    }
    return paymentClass;
  }

  private PaymentClassXml getPaymentClass(String response, ApplicationJson applicationJson){
    if(isNewExcavationPayment(applicationJson)){
      return WfsUtil.unmarshalWfs(response, PaymentNewClassXml.class);
    }
    else{
      return WfsUtil.unmarshalWfs(response, PaymentOldClassXml.class);
    }
  }

  @Override
  protected String getFeatureTypeName() {
    return FEATURE_TYPE_NAME;
  }

  @Override
  protected String getFeaturePropertyName() {
    return FEATURE_PROPERTY_NAME;
  }

  @Override
  protected String getFeatureTypeNameNew() {
    return getFeatureTypeName() + "_2022";
  }
}
