package fi.hel.allu.external.validation;

import fi.hel.allu.common.util.AnnualTimePeriod;
import fi.hel.allu.external.domain.ShortTermRentalExt;
import fi.hel.allu.servicecore.service.TerracePeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.format.DateTimeFormatter;

@Component
public class ShortTermRentalExtValidator implements Validator {

  private static final String NOT_WITHIN_PERIOD = "notWithinPeriod";
  private static final String SUMMER_TERRACE_NOT_WITHIN_PERIOD = "shorttermrental.summerterrace.notWithinPeriod";
  private static final String WINTER_TERRACE_NOT_WITHIN_PERIOD = "shorttermrental.winterterrace.notWithinPeriod";
  private static final String PARKLET_NOT_WITHIN_PERIOD = "shorttermrental.parklet.notWithinPeriod";

  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.");

  private final TerracePeriodService terracePeriodService;
  private final MessageSourceAccessor accessor;

  @Autowired
  ShortTermRentalExtValidator(
      TerracePeriodService terracePeriodService,
      MessageSource validationMessageSource) {
    this.terracePeriodService = terracePeriodService;
    accessor = new MessageSourceAccessor(validationMessageSource);
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return ShortTermRentalExt.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    ShortTermRentalExt application = (ShortTermRentalExt) target;
    if (application.getApplicationKind().isTerrace()) {
      validateTerracesAndParklets(application, errors);
    }
  }

  private void validateTerracesAndParklets(ShortTermRentalExt application, Errors errors) {
    switch (application.getApplicationKind()) {
      case SUMMER_TERRACE: {
        validateWithinPeriod(application, terracePeriodService.getSummerTerracePeriod(), SUMMER_TERRACE_NOT_WITHIN_PERIOD, errors);
        break;
      }

      case PARKLET: {
        validateWithinPeriod(application, terracePeriodService.getParkletPeriod(), PARKLET_NOT_WITHIN_PERIOD, errors);
        break;
      }

      case WINTER_TERRACE: {
        validateWithinPeriod(application, terracePeriodService.getWinterTerracePeriod(), WINTER_TERRACE_NOT_WITHIN_PERIOD, errors);
        break;
      }
    }
  }

  private void validateWithinPeriod(ShortTermRentalExt rental, AnnualTimePeriod period, String errorCode, Errors errors) {
    String[] params = new String[] { formatter.format(period.getPeriodStart()), formatter.format(period.getPeriodEnd()) };

    if (!period.isInAnnualPeriod(rental.getStartTime())) {
      errors.rejectValue("startTime", NOT_WITHIN_PERIOD, accessor.getMessage(errorCode, params));
    }

    if (!period.isInAnnualPeriod(rental.getEndTime())) {
      errors.rejectValue("endTime", NOT_WITHIN_PERIOD, accessor.getMessage(errorCode, params));
    }
  }
}
