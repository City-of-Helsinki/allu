package fi.hel.allu.model.domain;

import fi.hel.allu.common.domain.types.CustomerType;

public interface CustomerInterface {

    Integer getId();
    String getName();
    String getRegistryKey();
    String getOvt();
    CustomerType getType();
    boolean isActive();
    boolean isInvoicingOnly();
    String getSapCustomerNumber();
}