package fi.hel.allu.ui.domain;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * in Finnish: Hakemus
 */
public class ApplicationJson {

    private int id;
    @Valid
    private ProjectJson project;
    private String handler;
    @NotNull
    @Valid
    private CustomerJson customer;
    private String status;
    @NotBlank (message="{application.type}")
    private String type;
    @NotBlank (message="{application.name}")
    private String name;
    private ZonedDateTime creationTime;
    @NotNull
    @Valid
    private ApplicantJson applicant;
    @Valid
    private List<ContactJson> contactList;
    @Valid
    private LocationJson location;

    /**
     * in Finnish: Hakemuksen toimeksiantaja
     */
    public CustomerJson getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerJson customer) {
        this.customer = customer;
    }

    /**
     * in Finnish: Hakemuksen tunniste
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * in Finnish: Hanke, johon hakemus liittyy
     */
    public ProjectJson getProject() {
        return project;
    }

    public void setProject(ProjectJson project) {
        this.project = project;
    }

    /**
     * in Finnish: Hakemuksen käsittelijä
     */
    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    /**
     * in Finnish: Hakemuksen tila
     */
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * in Finnish: Hakemuksen tyyppi
     */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * in Finnish: Hakemuksen nimi
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * in Finnish: Hakemuksen luontiaika
     */
    public ZonedDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(ZonedDateTime creationTime) {
        this.creationTime = creationTime;
    }

    /**
     * in Finnish: Hakemuksen hakija
     */
    public ApplicantJson getApplicant() {
        return applicant;
    }

    public void setApplicant(ApplicantJson applicant) {
        this.applicant = applicant;
    }

    /**
     * in Finnish: Hakemuksen yhteyshenkilöt
     */
    public List<ContactJson> getContactList() {
        return contactList;
    }

    public void setContactList(List<ContactJson> contactList) {
        this.contactList = contactList;
    }

    /**
     * in Finnish: Hakemuksen sijainti
     */
    public LocationJson getLocation() {
        return location;
    }

    public void setLocation(LocationJson location) {
        this.location = location;
    }
}
