package fi.hel.allu.model.domain;

import java.util.Date;

/**
 * In Finnish: Hanke
 */
public class Project {

    /**
     * In Finnish: Hankkeen tunnus
     */
    private Integer projectId; // serial primary key

    /**
     * In Finnish: hankkeen omistaja
     */
    private Integer ownerId;

    /**
     * In Finnish: hankkeen yhteyshenkilä
     */
    private Integer contactId;

    /**
     * In Finnish: Hankkeen nimi
     */
    private String projectName; // Hankkeen nimi

    /**
     * In Finnish: alkupäivä
     */
    private Date startDate;

    /**
     * In Finnish: loppupäivä
     */
    private Date endDate;

    /**
     * In Finnish: lisätiedot
     */
    private String additionalInfo;

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public Integer getContactId() {
        return contactId;
    }

    public void setContactId(Integer contactId) {
        this.contactId = contactId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

}
