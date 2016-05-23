package fi.hel.allu.model.domain;

import java.util.Date;

/**
 * In Finnish: Hanke
 */
public class Project {

    private Integer projectId; // serial primary key

    private Integer ownerId;

    private Integer contactId;

    private String projectName;

    private Date startDate;

    private Date endDate;

    private String additionalInfo;

    /**
     * In Finnish: Hankkeen tunnus
     */
    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
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
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
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
