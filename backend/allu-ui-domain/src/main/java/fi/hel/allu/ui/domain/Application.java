package fi.hel.allu.ui.domain;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

/**
 * in Finnish: Hakemus
 */
public class Application{
    /**
     * Identifier of the application
     * in Finnish: Hakemuksen tunniste
     */
    private String id;

    /**
     * Name of the application
     * in Finnish: Hakemuksen nimi
     */
    @NotBlank (message="{application.name}")
    private String name;

    /**
     * Type of the application
     * in Finnish: Hakemuksen tyyppi
     */
    @NotBlank (message="{application.type}")
    private String type;

    /**
     * Application status
     * in Finnish: Hakemuksen tila
     */
    private String status;

    /**
     * Handler of the application
     * in Finnish: Hakemuksen käsittelijä
     */
    private String handler;

    /**
     * Information about the application
     * in Finnish: Hakemuksen kuvaus
     */
    private String information;

    /**
     *
     * in Finnish: Hakemuksen asiakas
     */
    @NotNull
    @Valid
    private Customer customer;

    /**
     * Creation date
     * in Finnish: Hakemuksen luontipäivämäärä
     */
    private ZonedDateTime createDate;

    /**
     * Application starting date
     * in Finnish: Hakemuksen aloituspäivämäärä
     */
    private ZonedDateTime startDate;

    /**
     * Project that application belongs to
     * in Finnish: Hanke, johon hakemus liittyy
     */
    @Valid
    private Project project;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public ZonedDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(ZonedDateTime createDate) {
        this.createDate = createDate;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void ZonedDateTime(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

}
