package fi.hel.allu.servicecore.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import fi.hel.allu.common.wfs.WfsUtil;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.LocationJson;
import fi.hel.allu.servicecore.service.geocode.PaymentClassXml;
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
  public String getPaymentClass(LocationJson location) {
    return executeWfsRequest(location);
  }

  @Override
  protected String parseResult(List<String> responses) {
    String paymentClass = UNDEFINED;
    for (String response : responses) {
      final PaymentClassXml paymentClassXml = WfsUtil.unmarshalWfs(response, PaymentClassXml.class);
      final List<PaymentClassXml.FeatureMember> paymentClasses = paymentClassXml.featureMember.stream()
          .sorted(Comparator.comparing(f -> f.paymentClass.getPaymentClass()))
          .collect(Collectors.toList());
      if (!paymentClasses.isEmpty()) {
        final String pc = paymentClasses.get(0).paymentClass.getPaymentClass();
        if (pc.compareTo(paymentClass) < 0) {
          paymentClass = pc;
        }
      }
    }
    return paymentClass;
  }

  @Override
  protected String getFeatureTypeName() {
    return FEATURE_TYPE_NAME;
  }

  @Override
  protected String getFeaturePropertyName() {
    return FEATURE_PROPERTY_NAME;
  }
}
