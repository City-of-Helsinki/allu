package fi.hel.allu.servicecore.service.geocode.featuremember;

import fi.hel.allu.servicecore.service.geocode.maksu.MaksuLuokka;

public interface FeatureClassMember {
  MaksuLuokka getMaksuluokka();

  String toString();

}
