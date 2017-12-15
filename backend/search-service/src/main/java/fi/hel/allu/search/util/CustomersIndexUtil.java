package fi.hel.allu.search.util;

import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.search.domain.ApplicationWithContactsES;
import fi.hel.allu.search.domain.ContactES;
import fi.hel.allu.search.domain.CustomerES;

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
    Map<String, Map<String, CustomerES>> customerRoleMap = new HashMap();
    customerRoleTypes.forEach(crt -> customerRoleMap.put(
        customerRoleTypeToPropertyName.get(crt), Collections.singletonMap("customer", customerES)));
    return Collections.singletonMap("customers", customerRoleMap);
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
}
