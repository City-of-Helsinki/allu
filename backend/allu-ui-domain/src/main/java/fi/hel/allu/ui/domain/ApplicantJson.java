package fi.hel.allu.ui.domain;

import javax.validation.Valid;

public class ApplicantJson {

    private int id;
    @Valid
    private PersonJson person;
    @Valid
    private OrganizationJson organization;


    /**
     * in Finnish: Hakijan tunniste
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * in Finnish: Hakijaan liittyvän henkilön tunniste
     */
    public PersonJson getPerson() {
        return person;
    }

    public void setPerson(PersonJson personJson) {
        this.person = personJson;
    }

    /**
     * in Finnish: Hakijaan liittyvän organisaation tunniste
     */
    public OrganizationJson getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationJson organization) {
        this.organization = organization;
    }
}
