package fi.hel.allu.model.util;

public class ApplicationCustomer {

    private Integer customerId;
    private Integer applicationId;

    public ApplicationCustomer(Integer customerId, Integer applicationId) {
        this.customerId = customerId;
        this.applicationId = applicationId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }
}
