package fi.hel.allu.servicecore.service;

import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.LocationJson;

public interface PaymentZoneService {

  String getPaymentZone(LocationJson location, ApplicationJson applicationJson);

}
