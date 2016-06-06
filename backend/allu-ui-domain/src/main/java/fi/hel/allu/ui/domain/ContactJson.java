package fi.hel.allu.ui.domain;

import javax.validation.Valid;

/**
 * in Finnish: Yhteyshenkilö
 */
public class ContactJson {
    private int id;
    @Valid
    private OrganizationJson organization;
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

    public OrganizationJson getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationJson organization) {
        this.organization = organization;
    }

    public PersonJson getPerson() {
        return person;
    }

    public void setPerson(PersonJson person) {
        this.person = person;
    }
}
