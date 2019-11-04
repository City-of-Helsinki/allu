package fi.hel.allu.search.util;

import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.search.domain.ApplicationWithContactsES;
import fi.hel.allu.search.domain.ContactES;
import fi.hel.allu.search.domain.CustomerES;
import fi.hel.allu.search.domain.CustomerWithContactsES;
import fi.hel.allu.search.domain.RoleTypedCustomerES;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * Utility methods for manipulating customers index.
 */
public class CustomersIndexUtil {

  private static final Map<CustomerRoleType, String> customerRoleTypeToPropertyName;

  static {
    customerRoleTypeToPropertyName = new HashMap<>();
    customerRoleTypeToPropertyName.put(CustomerRoleType.APPLICANT, "applicant");
    customerRoleTypeToPropertyName.put(CustomerRoleType.PROPERTY_DEVELOPER, "propertyDeveloper");
    customerRoleTypeToPropertyName.put(CustomerRoleType.CONTRACTOR, "contractor");
    customerRoleTypeToPropertyName.put(CustomerRoleType.REPRESENTATIVE, "representative");
  }

  /**
   * Creates a map structure, which allows partial updating of {@link fi.hel.allu.search.domain.CustomerWithContactsES} customer structure
   * inside an {@link fi.hel.allu.search.domain.ApplicationES}. It's assumed that the partially updated customer has the following path:
   * <ApplicationES>.customers.<CustomerRoleType>.customer.
   */
  public static Map getCustomerUpdateStructure(List<CustomerRoleType> customerRoleTypes, CustomerES customerES) {
    return getCustomerUpdateStructure(customerRoleTypes.stream().collect(
      Collectors.toMap(customerRoleType -> customerRoleType, __ -> customerES)));
  }

  public static Map<String, Object> getCustomerUpdateStructure(Map<CustomerRoleType, CustomerES> customerRoleTypeToCustomerES) {
    Map<String, Map<String, CustomerES>> customerRoleTypeToCustomer = customerRoleTypeToCustomerES.entrySet().stream().collect(
      Collectors.toMap(
        e -> customerRoleTypeToPropertyName.get(e.getKey()),
        e -> Collections.singletonMap("customer", e.getValue())
      )
    );
    return Collections.singletonMap("customers", customerRoleTypeToCustomer);
  }

  /**
   * Creates a map structure, which allows partial updating of {@link fi.hel.allu.search.domain.CustomerWithContactsES} contacts
   * structure inside an {@link fi.hel.allu.search.domain.ApplicationES}. It's assumed that the partially updated contacts have the
   * following path:
   * <ApplicationES>.customers.<CustomerRoleType>.contacts
   *
   * @param applicationWithContactsESs  List of <code>ApplicationWithContactES</code> objects whose update map is requested.
   * @return A map having application id as key and ES update structure as value.
   */
  public static Map<Integer, Map> getContactsUpdateStructure(
      List<ApplicationWithContactsES> applicationWithContactsESs) {
    Map<Integer, List<ApplicationWithContactsES>> applicationIdToATC = applicationWithContactsESs.stream()
        .collect(groupingBy(ApplicationWithContactsES::getApplicationId));
    return applicationIdToATC.entrySet().stream().collect(
        Collectors.toMap(entry -> entry.getKey(), entry -> getSingleContactsUpdateStructure(entry.getValue())));
  }

  /**
   * Creates a map structure, which allows partial updating of a single {@link fi.hel.allu.search.domain.CustomerWithContactsES} contacts
   * structure inside an {@link fi.hel.allu.search.domain.ApplicationES}. It's assumed that the partially updated contacts have the
   * following path:
   * <ApplicationES>.customers.<CustomerRoleType>.contacts
   *
   * @param applicationWithContactsESs  List of <code>ApplicationWithContactES</code> objects having the same application id.
   */
  private static Map getSingleContactsUpdateStructure(List<ApplicationWithContactsES> applicationWithContactsESs) {
    Map<String, Map<String, List<ContactES>>> customerRoleTypeToContacts = applicationWithContactsESs.stream().collect(Collectors.toMap(
        appWithContact -> customerRoleTypeToPropertyName.get(appWithContact.getCustomerRoleType()),
        appWithContact -> Collections.singletonMap("contacts", appWithContact.getContacts())));
    return Collections.singletonMap("customers", customerRoleTypeToContacts);
  }

  /**
   * Creates a map structure, which allows partial updating of {@link fi.hel.allu.search.domain.CustomerWithContactsES}
   * customer including their contacts related to an {@link fi.hel.allu.search.domain.ApplicationES}.
   *
   * It's assumed that the customer has the following path in elasticsearch: <ApplicationES>.customers.<CustomerRoleType>.customer.
   *
   * It's assumed that the contacts have the following path in elasticsearch: <ApplicationES>.customers.<CustomerRoleType>.contacts.
   *
   * @param customerRoleToCustomerWithContactsES customer structures mapped by their role in the application to be updated
   * @return an update structure that can be used for a partial update of an application
   */
  public static Map<String, Map<String, Map<String, Object>>> getCustomerWithContactsUpdateStructure(Map<CustomerRoleType, CustomerWithContactsES> customerRoleToCustomerWithContactsES) {
    Map<String, Map<String, Object>> customerRoleTypeToContacts = customerRoleToCustomerWithContactsES.entrySet().stream().collect(
      Collectors.toMap(
        e -> customerRoleTypeToPropertyName.get(e.getKey()),
        e -> {
          Map<String, Object> customerUpdateStructure = new HashMap<>();
          customerUpdateStructure.put("customer", e.getValue().getCustomer());
          customerUpdateStructure.put("contacts", e.getValue().getContacts());
          return customerUpdateStructure;
        }
      )
    );
    return Collections.singletonMap("customers", customerRoleTypeToContacts);
  }
}
