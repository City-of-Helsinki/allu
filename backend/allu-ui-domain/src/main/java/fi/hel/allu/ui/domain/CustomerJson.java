package fi.hel.allu.ui.domain;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;

/**
 * in Finnish: Toimeksiantaja
 */
public class CustomerJson {
    private int id;
    @NotBlank(message="{customer.type}")
    private String type;
    private String sapId;
    @Valid
    private PersonJson person;
    @Valid
    private OrganizationJson organization;

    /**
     * in Finnish: Toimeksiantajan tunniste
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
     * in Finnish: SAP-tunnus
     */
    public String getSapId() {
        return sapId;
    }

    public void setSapId(String sapId) {
        this.sapId = sapId;
    }

    /**
     * in Finnish: Toimeksiantajaan liittyvä henkilö
     */
    public PersonJson getPerson() {
        return person;
    }

    public void setPerson(PersonJson person) {
        this.person = person;
    }

    /**
     * in Finnish: Toimeksiantajaan liittyvän organisaatio
     */
    public OrganizationJson getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationJson organization) {
        this.organization = organization;
    }
}
