package fi.hel.allu.model.domain;

import fi.hel.allu.common.domain.types.CustomerRoleType;

import java.util.List;

public interface CustomerWithContactsI {

    CustomerRoleType getRoleType();
    <T extends CustomerInterface> T getCustomer();
    <U extends ContactInterface> List<U> getContacts();
}