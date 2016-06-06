package fi.hel.allu.ui.validator;

import fi.hel.allu.ui.domain.CustomerJson;

public class CustomerValidator {

    /**
     * Validate CustomerJson conditional fields
     * @param customerJson
     */
    public static void validateCustomer(CustomerJson customerJson) {
        switch (customerJson.getType()) {
            case "Person": //TODO: Replace with enum
                if (customerJson.getPerson() == null) {
                    throw new IllegalArgumentException("Customer.person is required, customer type is " + customerJson.getType());
                }
                if (customerJson.getOrganization() != null) {
                    throw new IllegalArgumentException("Customer.organization must be null, customer type is " + customerJson.getType());
                }
            break;
            case "Organization": //TODO: Replace with enum
                if (customerJson.getOrganization() == null) {
                    throw new IllegalArgumentException("Customer.organization is required, customer type is " + customerJson.getType());
                }
                if (customerJson.getPerson() != null) {
                    throw new IllegalArgumentException("Customer.person must be null, customer type is " + customerJson.getType());
                }
            break;
            default:
                throw new IllegalArgumentException("Invalid customer type");
        }
    }
}
