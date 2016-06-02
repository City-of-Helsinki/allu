package fi.hel.allu.model.domain;

import java.util.Date;

/**
 * In Finnish: Hanke
 */
public class Project {

    private Integer id;
    private Integer ownerId;
    private Integer contactId;
    private String name;
    private Date startDate;
    private Date endDate;
    private String additionalInfo;

    /**
     * In Finnish: Hankkeen tunnus
     */
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * In Finnish: hankkeen omistaja
     */
    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * In Finnish: hankkeen yhteyshenkilä
     */
    public Integer getContactId() {
        return contactId;
    }

    public void setContactId(Integer contactId) {
        this.contactId = contactId;
    }

    /**
     * In Finnish: Hankkeen nimi
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * In Finnish: alkupäivä
     */
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * In Finnish: loppupäivä
     */
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * In Finnish: lisätiedot
     */
    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

}
