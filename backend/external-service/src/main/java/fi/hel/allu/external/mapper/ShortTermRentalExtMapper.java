package fi.hel.allu.external.mapper;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.external.domain.ShortTermRentalExt;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.ShortTermRentalJson;
import fi.hel.allu.servicecore.validation.TranslationMessageTranslator;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ShortTermRentalExtMapper extends ApplicationExtMapper<ShortTermRentalExt> {

  private static final String PROMOTION_OR_SALES_NOT_BILLABLE = "shorttermrental.promotionOrSalesNotBillableReason";
  private static final String TERRACE_OR_PARKLET_NOT_BILLABLE = "shorttermrental.terraceNotBillableReason";

  private final TranslationMessageTranslator translator;

  @Autowired
  public ShortTermRentalExtMapper(TranslationMessageTranslator translator) {
    this.translator = translator;
  }

  @Override
  protected ShortTermRentalJson createExtension(ShortTermRentalExt rental) {
    ShortTermRentalJson extension = new ShortTermRentalJson();
    extension.setDescription(rental.getDescription());
    extension.setBillableSalesArea(rental.isBillableSalesArea());
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

    if (BooleanUtils.isTrue(application.getWithin80cmFromWall())) {
      applicationJson.setNotBillable(true);
      applicationJson.setNotBillableReason(notBillableReasonFor(application.getApplicationKind()));
    }
  }

  private String notBillableReasonFor(ApplicationKind kind) {
    switch (kind) {
      case PROMOTION_OR_SALES: {
        return translator.getTranslation(PROMOTION_OR_SALES_NOT_BILLABLE);
      }

      case SUMMER_TERRACE:
      case WINTER_TERRACE: {
        return translator.getTranslation(TERRACE_OR_PARKLET_NOT_BILLABLE);
      }

      default: {
        return null;
      }
    }
  }
}
