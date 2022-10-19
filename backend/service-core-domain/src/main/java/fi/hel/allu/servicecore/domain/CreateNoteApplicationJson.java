package fi.hel.allu.servicecore.domain;

import fi.hel.allu.common.domain.types.CustomerRoleType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(name = "CreateNoteApplicationJson", description = "Model for creating new notes")
public class CreateNoteApplicationJson extends CreateApplicationJson {
  public Map<CustomerRoleType, CreateCustomerWithContactsJson> getAllCustomersWithContactsByCustomerRoleType() {
    return super.getAllCustomersWithContactsByCustomerRoleType();
  }
}

