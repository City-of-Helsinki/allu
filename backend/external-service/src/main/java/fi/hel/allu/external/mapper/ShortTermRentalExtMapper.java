package fi.hel.allu.external.mapper;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.external.domain.ShortTermRentalExt;
import fi.hel.allu.servicecore.domain.ShortTermRentalJson;

@Component
public class ShortTermRentalExtMapper extends ApplicationExtMapper<ShortTermRentalExt> {

  @Override
  protected ShortTermRentalJson createExtension(ShortTermRentalExt rental) {
    ShortTermRentalJson extension = new ShortTermRentalJson();
    extension.setDescription(rental.getDescription());
    return extension;
  }

  @Override
  protected ApplicationType getApplicationType() {
    return ApplicationType.SHORT_TERM_RENTAL;
  }

  @Override
  protected ApplicationKind getApplicationKind(ShortTermRentalExt rental) {
    return rental.getApplicationKind();
  }

  @Override
  protected List<Integer> getFixedLocationIds(ShortTermRentalExt rental) {
    return rental.getFixedLocationId() != null ? Collections.singletonList(rental.getFixedLocationId()) : null;
  }
}
