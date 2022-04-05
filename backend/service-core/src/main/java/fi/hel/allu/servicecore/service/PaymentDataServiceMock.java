package fi.hel.allu.servicecore.service;

import fi.hel.allu.servicecore.domain.ApplicationJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import fi.hel.allu.servicecore.domain.LocationJson;

@Profile(value = "DEV")
@Service
public class PaymentDataServiceMock implements PaymentZoneService, PaymentClassService {

  private static final Logger logger = LoggerFactory.getLogger(PaymentDataServiceMock.class);

  @Override
  public String getPaymentZone(LocationJson location, ApplicationJson applicationJson) {
    final String paymentZone = "1";
    logger.info("PaymentDataServiceMock: returning payment zone {}", paymentZone);
    return paymentZone;
  }

  @Override
  public String getPaymentClass(LocationJson location, ApplicationJson applicationJson) {
    final String paymentClass = "3";
    logger.info("PaymentDataServiceMock: returning payment class {}", paymentClass);
    return paymentClass;
  }
}
