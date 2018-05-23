package fi.hel.allu.external.validation;

import org.geolatte.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import fi.hel.allu.external.domain.ApplicationExt;
import fi.hel.allu.servicecore.service.LocationService;

@Component
public class ApplicationExtGeometryValidator implements Validator {

  private final LocationService locationService;
  private final MessageSourceAccessor accessor;

  @Autowired
  ApplicationExtGeometryValidator(
      LocationService locationService,
      MessageSource validationMessageSource) {
    this.locationService = locationService;
    accessor = new MessageSourceAccessor(validationMessageSource);
  }

  private static final String ERROR_CODE =  "application.locations.geometry.invalid";

  @Override
  public boolean supports(Class<?> clazz) {
    return ApplicationExt.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    ApplicationExt application = (ApplicationExt) target;
    if (!isValidGeometry(application.getGeometry())) {
      errors.rejectValue("geometry", ERROR_CODE, accessor.getMessage(ERROR_CODE));
    }
  }

  private boolean isValidGeometry(Geometry geometry) {
    return locationService.isValidGeometry(geometry);
  }
}
