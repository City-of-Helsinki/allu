package fi.hel.allu.ui.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.hel.allu.common.validator.NotFalse;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;

@NotFalse(rules = {"person, validatePersonTypeHasPersonObject, {type.person.emptyperson}",
        "person, validatePersonTypeHasNotOrganizationObject, {type.person.notemptyorganization}",
        "organization, validateOrganizationTypeHasOrganizationObject, {type.organization.emptyorganization}",
        "organization, validateOrganizationTypeHasNotPersonObject, {type.organization.notemptyperson}"})
public class TypeJson {
    @NotEmpty(message="{type.notnull}")
    private String type;
    @Valid
    private PersonJson person;
    @Valid
    private OrganizationJson organization;
    /**
     * in Finnish: tyyppi, ihminen tai yritys/yhteisö
     */
    public String getType() {
        return type;
    }

    public void setType(String type) {
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
        if (getType() != null) {
            switch (getType()) {
                case "Person": //TODO: Replace with enum
                    if (getPerson() == null) {
                        return false;
                    }
                case "Organization": //TODO: Replace with enum
                    return true;
            }
        }
        return true;
    }

    @JsonIgnore
    public boolean getValidatePersonTypeHasNotOrganizationObject() {
        if (getType() != null) {
            switch (getType()) {
                case "Person": //TODO: Replace with enum
                    if (getOrganization() != null) {
                        return false;
                    }
                    break;
                case "Organization": //TODO: Replace with enum
                    return true;
            }
        }
        return true;
    }

    @JsonIgnore
    public boolean getValidateOrganizationTypeHasNotPersonObject() {
        if (getType() != null) {
            switch (getType()) {
                case "Person": //TODO: Replace with enum
                    return true;
                case "Organization": //TODO: Replace with enum
                    if (getPerson() != null) {
                        return false;
                    }
                    break;
            }
        }
        return true;
    }

    @JsonIgnore
    public boolean getValidateOrganizationTypeHasOrganizationObject() {
        if (getType() != null) {
            switch (getType()) {
                case "Person": //TODO: Replace with enum
                    return true;
                case "Organization": //TODO: Replace with enum
                    if (getOrganization() == null) {
                        return false;
                    }
                    break;

            }
        }
        return true;
    }
}
