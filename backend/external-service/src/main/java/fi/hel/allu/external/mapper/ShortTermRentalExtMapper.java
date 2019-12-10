package fi.hel.allu.external.mapper;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.external.domain.ShortTermRentalExt;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.ShortTermRentalJson;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

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
    return rental.getFixedLocationIds();
  }

  @Override
  protected void mapTypeSpecificData(ShortTermRentalExt application, ApplicationJson applicationJson) {
    Optional.ofNullable(application.getRecurringEndYear())
      .map(recurringEndYear -> application.getEndTime().withYear(recurringEndYear))
      .ifPresent(recurringEnd -> applicationJson.setRecurringEndTime(recurringEnd));
  }
}
