package fi.hel.allu.external.mapper;

import java.util.List;
import java.util.stream.Collectors;

import fi.hel.allu.external.domain.FixedLocationExt;
import fi.hel.allu.servicecore.domain.FixedLocationJson;

public class FixedLocationMapper {

  public static List<FixedLocationExt> mapToExt(List<FixedLocationJson> fixedLocationList) {
    return fixedLocationList.stream().map(f -> new FixedLocationExt(f.getId(), f.getArea(), f.getSection(),
        f.getApplicationKind(), f.getGeometry())).collect(Collectors.toList());
  }
}
