package fi.hel.allu.external.domain;

import fi.hel.allu.common.domain.types.CustomerRoleType;

import java.util.List;

/**
 * Allu application customer with its related contacts exposed to external users.
 */
public class CustomerWithContactsExt {
  CustomerRoleType roleType;
  private Integer customer;
  List<Integer> contacts;
}
