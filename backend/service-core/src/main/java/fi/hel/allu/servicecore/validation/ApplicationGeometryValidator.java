package fi.hel.allu.servicecore.validation;

import java.util.Locale;

import javax.annotation.PostConstruct;

import org.geolatte.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.service.LocationService;

@Component
public class ApplicationGeometryValidator implements Validator {

  @Autowired
  private LocationService locationService;

  @Autowired
  private MessageSource validationMessageSource;

  private MessageSourceAccessor accessor;

  private static final String ERROR_CODE =  "application.locations.geometry.invalid";

  @Override
  public boolean supports(Class<?> clazz) {
    return ApplicationJson.class.isAssignableFrom(clazz);
  }

  @PostConstruct
  private void init() {
      accessor = new MessageSourceAccessor(validationMessageSource);
  }

  @Override
  public void validate(Object target, Errors errors) {
    ApplicationJson application = (ApplicationJson) target;
    for (int i = 0; i < application.getLocations().size(); i++)
      if (!isValidGeometry(application.getLocations().get(i).getGeometry())) {
        errors.rejectValue("locations[" + i + "].geometry", ERROR_CODE, accessor.getMessage(ERROR_CODE));
      }
  }

  private boolean isValidGeometry(Geometry geometry) {
    return locationService.isValidGeometry(geometry);
  }

}
