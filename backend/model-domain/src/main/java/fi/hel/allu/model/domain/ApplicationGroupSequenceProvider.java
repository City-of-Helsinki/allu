package fi.hel.allu.model.domain;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;

import fi.hel.allu.common.domain.types.StatusType;

/**
 *  Group sequence provider for application validation.
 *
 */
public class ApplicationGroupSequenceProvider implements DefaultGroupSequenceProvider<Application> {

  @Override
  public List<Class<?>> getValidationGroups(Application application) {
    List<Class<?>> result = new ArrayList<>();
    result.add(Application.class);
    if (application != null && StatusType.PRE_RESERVED != application.getStatus()) {
     result.add(Application.Complete.class);
    }
    return result;
  }

}
