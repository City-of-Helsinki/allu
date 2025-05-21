package fi.hel.allu.common.controller.converter;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.exception.InvalidApplicationTypeException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ApplicationTypeConverter implements Converter<String, ApplicationType> {
  @Override
  public ApplicationType convert(String source) {
    try {
      return ApplicationType.valueOf(source.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new InvalidApplicationTypeException(source);
    }
  }
}
