package fi.hel.allu.servicecore.validation;

import java.util.Locale;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import fi.hel.allu.model.domain.Location;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.mapper.LocationMapper;
import fi.hel.allu.servicecore.service.LocationService;

@Component
public class ApplicationGeometryValidator implements Validator {

  @Autowired
  private LocationService locationService;

  @Autowired
  private MessageSource messageSource;

  private MessageSourceAccessor accessor;

  private static final String ERROR_CODE =  "application.locations.geometry";

  @Override
  public boolean supports(Class<?> clazz) {
    return ApplicationJson.class.isAssignableFrom(clazz);
  }

  @PostConstruct
  private void init() {
      accessor = new MessageSourceAccessor(messageSource, Locale.getDefault());
  }

  @Override
  public void validate(Object target, Errors errors) {
    ApplicationJson application = (ApplicationJson) target;
    for (int i = 0; i < application.getLocations().size(); i++)
      if (!hasValidGeometry(LocationMapper.createLocationModel(application.getId(), application.getLocations().get(i)))) {
        errors.rejectValue("locations[" + i + "].geometry", ERROR_CODE, accessor.getMessage(ERROR_CODE));
      }
  }

  private boolean hasValidGeometry(Location location) {
    return locationService.hasValidGeometry(location);
  }

}
