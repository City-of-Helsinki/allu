package fi.hel.allu.ui.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.hel.allu.common.types.CustomerType;
import fi.hel.allu.common.validator.NotFalse;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@NotFalse(rules = {"person, validatePersonTypeHasPersonObject, {type.person.emptyperson}",
    "person, validatePersonTypeHasNotOrganizationObject, {type.person.notemptyorganization}",
    "organization, validateOrganizationTypeHasOrganizationObject, {type.organization.emptyorganization}",
    "organization, validateOrganizationTypeHasNotPersonObject, {type.organization.notemptyperson}"})
public class TypeJson {
  @NotNull(message = "{type.notnull}")
  private CustomerType type;
  @Valid
  private PersonJson person;
  @Valid
  private OrganizationJson organization;

  /**
   * in Finnish: tyyppi, ihminen tai yritys/yhteisö
   */
  public CustomerType getType() {
    return type;
  }

  public void setType(CustomerType type) {
    this.type = type;
  }

  /**
   * in Finnish: liittyvä henkilö
   */
  public PersonJson getPerson() {
    return person;
  }

  public void setPerson(PersonJson person) {
    this.person = person;
  }

  /**
   * in Finnish: liittyvä organisaatio
   */
  public OrganizationJson getOrganization() {
    return organization;
  }

  public void setOrganization(OrganizationJson organization) {
    this.organization = organization;
  }

  @JsonIgnore
  public boolean getValidatePersonTypeHasPersonObject() {
    switch (getType()) {
      case Person:
        return getPerson() != null;
      case Company:
      case Association:
        return true;
      default:
        return true;
    }
  }

  @JsonIgnore
  public boolean getValidatePersonTypeHasNotOrganizationObject() {
    switch (getType()) {
      case Person:
        return getOrganization() == null;
      case Company:
      case Association:
        return true;
      default:
        return true;
    }
  }

  @JsonIgnore
  public boolean getValidateOrganizationTypeHasNotPersonObject() {
    switch (getType()) {
      case Person:
        return true;
      case Company:
      case Association:
        return getPerson() == null;
      default:
        return true;
    }
  }

  @JsonIgnore
  public boolean getValidateOrganizationTypeHasOrganizationObject() {
    switch (getType()) {
      case Person:
        return true;
      case Company:
      case Association:
        return getOrganization() != null;
      default:
        return true;
    }
  }
}
