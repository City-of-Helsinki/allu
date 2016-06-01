package fi.hel.allu.model.domain;

/**
 * in Finnish: Hakija
 */
public class Applicant {

    /**
     * in Finnish: Hakijan tunniste
     */
    private Integer id;

    /**
     * in Finnish: Hakijaan liittyvän henkilön tunniste
     */
    private Integer personId;

    /**
     * in Finnish: Hakijaan liittyvän organisaation tunniste
     */
    private Integer organizationId;


    /**
     * in Finnish: Hakijan tunniste
     */
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * in Finnish: Hakijaan liittyvän henkilön tunniste
     */
    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }


    /**
     * in Finnish: Hakijaan liittyvän organisaation tunniste
     */
    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }
}
