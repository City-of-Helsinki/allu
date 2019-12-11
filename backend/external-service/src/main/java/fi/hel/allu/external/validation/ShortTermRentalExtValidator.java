package fi.hel.allu.external.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.external.config.ApplicationProperties;
import fi.hel.allu.external.domain.ShortTermRentalExt;

@Component
public class ShortTermRentalExtValidator implements Validator {

  private final ApplicationProperties applicationProperties;
  private final MessageSourceAccessor accessor;

  @Autowired
  ShortTermRentalExtValidator(
      ApplicationProperties applicationProperties,
      MessageSource validationMessageSource) {
    this.applicationProperties = applicationProperties;
    accessor = new MessageSourceAccessor(validationMessageSource);
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return ShortTermRentalExt.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    ShortTermRentalExt application = (ShortTermRentalExt) target;
  }
}
