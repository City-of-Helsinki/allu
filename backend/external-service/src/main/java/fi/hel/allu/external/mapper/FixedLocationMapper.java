package fi.hel.allu.external.mapper;

import java.util.List;
import java.util.stream.Collectors;

import fi.hel.allu.external.domain.FixedLocationExt;
import fi.hel.allu.servicecore.domain.FixedLocationJson;

public class FixedLocationMapper {

  public static List<FixedLocationExt> mapToExt(List<FixedLocationJson> fixedLocationList) {
    return fixedLocationList.stream().map(f -> mapToExt(f)).collect(Collectors.toList());
  }

  public static FixedLocationExt mapToExt(FixedLocationJson fixedLocation) {
    return new FixedLocationExt(fixedLocation.getId(), fixedLocation.getArea(), fixedLocation.getSection(),
        fixedLocation.getApplicationKind(), fixedLocation.getGeometry());
  }
}
