package fi.hel.allu.servicecore.domain;

import fi.hel.allu.common.domain.types.CustomerRoleType;
import io.swagger.annotations.ApiModel;

import java.util.Map;

@ApiModel(value = "CreateNoteApplicationJson", description = "Model for creating new notes")
public class CreateNoteApplicationJson extends CreateApplicationJson {
  public Map<CustomerRoleType, Integer> getAllCustomerIdsByCustomerRoleType() {
    return super.getAllCustomerIdsByCustomerRoleType();
  }
}

