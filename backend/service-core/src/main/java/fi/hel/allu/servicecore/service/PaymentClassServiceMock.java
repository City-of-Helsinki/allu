package fi.hel.allu.servicecore.service;

import fi.hel.allu.servicecore.domain.LocationJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile(value = "DEV")
@Service
public class PaymentClassServiceMock implements PaymentClassService {

  private static final Logger logger = LoggerFactory.getLogger(PaymentClassServiceMock.class);

  @Override
  public String getPaymentClass(LocationJson location) {
    logger.info("PaymentClassServiceMock: returning payment class 3");
    return "3";
  }
}
