package fi.hel.allu.ui.domain;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * in Finnish: Yhteyshenkilö
 */
public class ContactJson {
    private int id;
    private int organizationId;
    @NotNull(message="{contact.person.notnull}")
    @Valid
    private PersonJson person;


    /**
     * in Finnish: Yhteyshenkilön tunniste
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * in Finnish: Yhteyshenkilön organisaation tunniste
     */
    public int getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(int organizationId) {
        this.organizationId = organizationId;
    }

    public PersonJson getPerson() {
        return person;
    }

    public void setPerson(PersonJson person) {
        this.person = person;
    }
}
