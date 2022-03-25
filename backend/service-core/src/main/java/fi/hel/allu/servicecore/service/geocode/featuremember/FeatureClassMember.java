package fi.hel.allu.servicecore.service.geocode.featuremember;

import fi.hel.allu.servicecore.service.geocode.paymentlevel.PaymentLevelClass;

public interface FeatureClassMember {
  PaymentLevelClass getMaksuluokka();

  String toString();

}
