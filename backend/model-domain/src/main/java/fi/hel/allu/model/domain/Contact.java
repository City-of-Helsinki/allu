package fi.hel.allu.model.domain;

/**
 * in Finnish: Yhteyshenkilö
 */
public class Contact {
    /**
     * in Finnish: Yhteyshenkilön tunniste
     */
    private Integer id;

    /**
     * in Finnish: Yhteyshenkilön organisaation tunniste
     */
    private Integer organizationId;

    /**
     * in Finnish: Yhteyshenkilön henkilön tunniste
     */
    private Integer personId;


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
     * in Finnish: Yhteyshenkilön henkilön tunniste
     */
    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }
}
