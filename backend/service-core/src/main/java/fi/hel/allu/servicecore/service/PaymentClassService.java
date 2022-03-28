package fi.hel.allu.servicecore.service;

import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.LocationJson;

public interface PaymentClassService {
  public String getPaymentClass(LocationJson location, ApplicationJson applicationJson);
}
