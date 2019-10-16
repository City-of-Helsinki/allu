package fi.hel.allu.servicecore.domain.validator;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.BaseApplicationJson;
import fi.hel.allu.servicecore.domain.EventJson;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validates that if ApplicationJson contains an OUTDOOREVENT then nature must not be null.
 * For other events (currently just PROMOTION) it is ok that nature is null.
 */
public class ApplicationJsonValidator implements ConstraintValidator<ValidApplication, Object> {

  @Override
  public void initialize(ValidApplication a) {
  }

  @Override
  public boolean isValid(Object t, ConstraintValidatorContext cvc) {
    if (t == null || !(t instanceof BaseApplicationJson)) {
      return false;
    }

    final BaseApplicationJson app = (BaseApplicationJson)t;

    if (ApplicationType.EVENT != app.getType()) {
      return true;
    }

    if (ApplicationKind.OUTDOOREVENT == app.getKind()) {
      final EventJson event = (EventJson)app.getExtension();
      if (event.getNature() == null) {
        return false;
      }
    }
    return true;
  }
}
