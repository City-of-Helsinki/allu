package fi.hel.allu.ui.mapper;

import fi.hel.allu.model.domain.DefaultRecipient;
import fi.hel.allu.ui.domain.DefaultRecipientJson;
import org.springframework.stereotype.Component;

/**
 * Mapper for mapping Default recipients from Json to Model and back
 */
@Component
public class DefaultRecipientMapper {
  public DefaultRecipient createModel(DefaultRecipientJson json) {
    DefaultRecipient model = new DefaultRecipient();
    model.setId(json.getId());
    model.setEmail(json.getEmail());
    model.setApplicationType(json.getApplicationType());
    return model;
  }

  public DefaultRecipientJson createJson(DefaultRecipient model) {
    DefaultRecipientJson json = new DefaultRecipientJson();
    json.setId(model.getId());
    json.setEmail(model.getEmail());
    json.setApplicationType(model.getApplicationType());
    return json;
  }
}
