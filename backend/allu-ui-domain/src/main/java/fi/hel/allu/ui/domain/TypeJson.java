package fi.hel.allu.ui.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.hel.allu.common.types.ApplicantType;
import fi.hel.allu.common.validator.NotFalse;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@NotFalse(rules = {"person, validatePersonTypeHasPersonObject, {type.person.emptyperson}",
    "person, validatePersonTypeHasNotOrganizationObject, {type.person.notemptyorganization}",
    "organization, validateOrganizationTypeHasOrganizationObject, {type.organization.emptyorganization}",
    "organization, validateOrganizationTypeHasNotPersonObject, {type.organization.notemptyperson}"})
public class TypeJson {
  @NotNull(message = "{type.notnull}")
  private ApplicantType type;
  @Valid
  private PersonJson person;
  @Valid
  private OrganizationJson organization;

  /**
   * in Finnish: tyyppi, ihminen tai yritys/yhteisö
   */
  public ApplicantType getType() {
    return type;
  }

  public void setType(ApplicantType type) {
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
  public String getName() {
    switch (getType()) {
      case PERSON:
        return getPerson().getName();
      case COMPANY:
        return getOrganization().getName();
      case ASSOCIATION:
        return getOrganization().getName();
      default:
        throw new UnsupportedOperationException("Requested name of applicant, which is neither person nor organization");
    }
  }

  @JsonIgnore
  public boolean getValidatePersonTypeHasPersonObject() {
    switch (getType()) {
      case PERSON:
        return getPerson() != null;
      case COMPANY:
      case ASSOCIATION:
        return true;
      default:
        return true;
    }
  }

  @JsonIgnore
  public boolean getValidatePersonTypeHasNotOrganizationObject() {
    switch (getType()) {
      case PERSON:
        return getOrganization() == null;
      case COMPANY:
      case ASSOCIATION:
        return true;
      default:
        return true;
    }
  }

  @JsonIgnore
  public boolean getValidateOrganizationTypeHasNotPersonObject() {
    switch (getType()) {
      case PERSON:
        return true;
      case COMPANY:
      case ASSOCIATION:
        return getPerson() == null;
      default:
        return true;
    }
  }

  @JsonIgnore
  public boolean getValidateOrganizationTypeHasOrganizationObject() {
    switch (getType()) {
      case PERSON:
        return true;
      case COMPANY:
      case ASSOCIATION:
        return getOrganization() != null;
      default:
        return true;
    }
  }
}
