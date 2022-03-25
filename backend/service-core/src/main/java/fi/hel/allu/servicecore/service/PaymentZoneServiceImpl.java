package fi.hel.allu.servicecore.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import fi.hel.allu.common.wfs.WfsUtil;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.LocationJson;
import fi.hel.allu.servicecore.service.geocode.PaymentZoneXml;
import fi.hel.allu.servicecore.util.AsyncWfsRestTemplate;


@Profile("!DEV")
@Service
public class PaymentZoneServiceImpl extends AbstractWfsPaymentDataService implements PaymentZoneService {

  private static final String DEFAULT_PAYMENT_ZONE = "2";
  private static final String FEATURE_TYPE_NAME = "Terassit_maksuvyohyke";
  private static final String FEATURE_PROPERTY_NAME = "maksuvyohyke";

  @Autowired
  public PaymentZoneServiceImpl(ApplicationProperties applicationProperties, AsyncWfsRestTemplate restTemplate) {
    super(applicationProperties, restTemplate);
  }

  @Override
  public String getPaymentZone(LocationJson location) {
    return executeWfsRequest(location);
  }

  @Override
  protected String parseResult(List<String> responses, LocationJson locationJson) {
    return responses.stream()
        .map(r -> WfsUtil.unmarshalWfs(r, PaymentZoneXml.class))
        .filter(p -> p.featureMember != null)
        .flatMap(x -> x.featureMember.stream())
        .map(p -> p.paymentLevelZone.getPayment())
        .filter(StringUtils::isNotBlank)
        .sorted()
        .findFirst().orElse(DEFAULT_PAYMENT_ZONE);
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
    return getFeatureTypeName();
  }
}
