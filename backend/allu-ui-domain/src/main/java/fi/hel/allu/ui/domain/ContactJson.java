package fi.hel.allu.ui.domain;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * in Finnish: Yhteyshenkilö
 */
public class ContactJson {
    private Integer id;
    private Integer organizationId;
    @NotNull(message="{contact.person.notnull}")
    @Valid
    private PersonJson person;


    /**
     * in Finnish: Yhteyshenkilön tunniste
     */
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * in Finnish: Yhteyshenkilön organisaation tunniste
     */
    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }


    /**
     * in Finnish: YHteyshenkilön henkilötiedot
     */
    public PersonJson getPerson() {
        return person;
    }

    public void setPerson(PersonJson person) {
        this.person = person;
    }
}
