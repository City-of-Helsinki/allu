package fi.hel.allu.servicecore.service.geocode.featuremember;

import fi.hel.allu.servicecore.service.geocode.paymentlevel.PaymentLevel;

public interface FeatureClassMember {
  PaymentLevel getMaksuluokka();

  String toString();

}
