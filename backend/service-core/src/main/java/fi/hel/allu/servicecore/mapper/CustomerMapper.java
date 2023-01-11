package fi.hel.allu.servicecore.mapper;

import fi.hel.allu.common.domain.types.CodeSetType;
import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.common.domain.types.RoleType;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.search.domain.ContactES;
import fi.hel.allu.search.domain.CustomerES;
import fi.hel.allu.search.domain.CustomerWithContactsES;
import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.servicecore.service.CodeSetService;
import fi.hel.allu.servicecore.service.UserService;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class CustomerMapper {

  private final UserService userService;
  private final CodeSetService codeSetService;

  private static final Set<RoleType> canSeeSsn = new HashSet<>(Arrays.asList(
      RoleType.ROLE_CREATE_APPLICATION,
      RoleType.ROLE_PROCESS_APPLICATION,
      RoleType.ROLE_INVOICING));

  public CustomerMapper(UserService userService, CodeSetService codeSetService) {
    this.userService = userService;
    this.codeSetService = codeSetService;
  }

  public CustomerJson createCustomerJson(Customer customer) {
    if (customer == null) {
      return null;
    }
    CustomerJson customerJson = defaultCustomerJson(customer);
    if (customer.getCountryId() != null) {
      customerJson.setCountry(codeSetService.findById(customer.getCountryId()).getCode());
    }
    return customerJson;
  }

  public CustomerJson createCustomerJson(Customer customer, Map<Integer, CodeSet> codeSetMap) {
    if (customer == null) {
      return null;
    }
    CustomerJson customerJson = defaultCustomerJson(customer);
    if (customer.getCountryId() != null && codeSetMap.containsKey(customer.getCountryId())) {
      customerJson.setCountry(codeSetMap.get(customer.getCountryId()).getCode());
    }
    return customerJson;
  }

  public CustomerJson defaultCustomerJson(Customer customer) {
    CustomerJson customerJson = new CustomerJson();
    customerJson.setId(customer.getId());
    customerJson.setType(customer.getType());
    customerJson.setName(customer.getName());
    customerJson.setRegistryKey(getVisibleRegistryKey(customer.getType(), customer.getRegistryKey()));
    customerJson.setOvt(customer.getOvt());
    customerJson.setPhone(customer.getPhone());
    customerJson.setEmail(customer.getEmail());
    customerJson.setPostalAddress(ApplicationCommonMapper.createPostalAddressJson(customer.getPostalAddress()));
    customerJson.setActive(customer.isActive());
    customerJson.setSapCustomerNumber(customer.getSapCustomerNumber());
    customerJson.setInvoicingProhibited(customer.isInvoicingProhibited());
    customerJson.setInvoicingOperator(customer.getInvoicingOperator());
    customerJson.setInvoicingOnly(customer.isInvoicingOnly());
    customerJson.setProjectIdentifierPrefix(customer.getProjectIdentifierPrefix());
    return customerJson;
  }

  public Customer createCustomerModel(CustomerJson customerJson) {
    if (customerJson == null) {
      return null;
    }
    Customer customerModel = new Customer();
    customerModel.setId(customerJson.getId());
    customerModel.setType(customerJson.getType());
    customerModel.setName(customerJson.getName());
    customerModel.setRegistryKey(customerJson.getRegistryKey());
    customerModel.setOvt(customerJson.getOvt());
    customerModel.setPhone(customerJson.getPhone());
    customerModel.setEmail(customerJson.getEmail());
    customerModel.setPostalAddress(ApplicationCommonMapper.createPostalAddressModel(customerJson.getPostalAddress()));
    customerModel.setActive(customerJson.isActive());
    customerModel.setSapCustomerNumber(customerJson.getSapCustomerNumber());
    customerModel.setInvoicingProhibited(customerJson.isInvoicingProhibited());
    customerModel.setInvoicingOperator(customerJson.getInvoicingOperator());
    customerModel.setInvoicingOnly(customerJson.isInvoicingOnly());
    if (customerJson.getCountry() != null) {
      customerModel.setCountryId(codeSetService.findByTypeAndCode(CodeSetType.Country, customerJson.getCountry().toUpperCase()).getId());
    }
    customerModel.setProjectIdentifierPrefix(customerJson.getProjectIdentifierPrefix());
    return customerModel;
  }

  /**
   * Map the given Contact object into ContactJson
   *
   * @param c Contact object
   * @return Ui-domain Contact representation of the parameter
   */
  public ContactJson createContactJson(Contact c) {
    ContactJson json = new ContactJson();
    json.setId(c.getId());
    json.setCustomerId(c.getCustomerId());
    json.setName(c.getName());
    if (c.getPostalAddress() != null) {
      // TODO: refactor when contact starts using PostalAddressJson
      json.setStreetAddress(c.getPostalAddress().getStreetAddress());
      json.setPostalCode(c.getPostalAddress().getPostalCode());
      json.setCity(c.getPostalAddress().getCity());
    }
    json.setEmail(c.getEmail());
    json.setPhone(c.getPhone());
    json.setActive(c.isActive());
    json.setOrderer(c.getOrderer());
    return json;
  }

  public Contact createContactModel(ContactJson json) {
    Contact contact = new Contact();
    contact.setId(json.getId());
    contact.setCustomerId(json.getCustomerId());
    contact.setName(json.getName());
    if (json.getStreetAddress() != null || json.getPostalCode() != null || json.getCity() != null) {
      // TODO: refactor when contact starts using PostalAddressJson
      contact.setPostalAddress(new PostalAddress(json.getStreetAddress(), json.getPostalCode(), json.getCity()));
    }
    contact.setEmail(json.getEmail());
    contact.setPhone(json.getPhone());
    contact.setIsActive(json.isActive());
    contact.setOrderer(json.getOrderer());
    return contact;
  }

  public <T extends CustomerInterface> CustomerES createCustomerES(T customerJson) {
    if (customerJson != null) {
      return new CustomerES(
          customerJson.getId(),
          customerJson.getName(),
          customerJson.getRegistryKey(),
          customerJson.getOvt(),
          customerJson.getType(),
          customerJson.isActive(),
          customerJson.isInvoicingOnly(),
          customerJson.getSapCustomerNumber());
    } else {
      return null;
    }
  }

  public <T extends ContactInterface> List<ContactES> createContactES(List<T> contacts) {
    if (contacts != null) {
      return contacts.stream()
          .map(c -> new ContactES(c.getId(), c.getName(), c.isActive()))
          .collect(Collectors.toList());
    } else {
      return null;
    }
  }

  public <T extends CustomerWithContactsI> CustomerWithContactsES createWithContactsES(T customerWithContacts) {
    CustomerWithContactsES customerWithContactsES = new CustomerWithContactsES();
    customerWithContactsES.setCustomer(createCustomerES(customerWithContacts.getCustomer()));
    customerWithContactsES.setContacts(createContactES(customerWithContacts.getContacts()));
    return customerWithContactsES;
  }

  public List<CustomerWithContactsJson> createWithContactsJson(Application application) {
    List<CustomerWithContacts> customersWithContacts = application.getCustomersWithContacts();
    List<CustomerWithContactsJson> customerWithContactsJsons = new ArrayList<>();

    customersWithContacts.forEach(cwc -> {
      CustomerWithContactsJson customerWithContactsJson = createWithContactsJson(cwc);
      customerWithContactsJsons.add(customerWithContactsJson);
    });
    return customerWithContactsJsons;
  }

  public List<CustomerWithContactsJson> createWithContactsJson(Application application,
                                                               Map<Integer, CodeSet> codesets) {
    List<CustomerWithContacts> customersWithContacts = application.getCustomersWithContacts();
    List<CustomerWithContactsJson> customerWithContactsJsons = new ArrayList<>();

    customersWithContacts.forEach(cwc -> {
      CustomerWithContactsJson customerWithContactsJson = createWithContactsJson(cwc, codesets);
      customerWithContactsJsons.add(customerWithContactsJson);
    });
    return customerWithContactsJsons;
  }

  public CustomerWithContactsJson createWithContactsJson(CustomerWithContacts cwc) {
    if (cwc == null) {
      return null;
    }
    CustomerWithContactsJson customerWithContactsJson = new CustomerWithContactsJson();
    customerWithContactsJson.setContacts(cwc.getContacts().stream()
                                                 .map(c -> createContactJson(c))
                                                 .collect(Collectors.toList()));
    customerWithContactsJson.setCustomer(createCustomerJson(cwc.getCustomer()));
    customerWithContactsJson.setRoleType(cwc.getRoleType());
    return customerWithContactsJson;
  }


  public CustomerWithContactsJson createWithContactsJson(CustomerWithContacts cwc, Map<Integer, CodeSet> codesets) {
    if (cwc == null) {
      return null;
    }
    CustomerWithContactsJson customerWithContactsJson = new CustomerWithContactsJson();
    customerWithContactsJson.setContacts(cwc.getContacts().stream()
                                                 .map(c -> createContactJson(c))
                                                 .collect(Collectors.toList()));
    customerWithContactsJson.setCustomer(createCustomerJson(cwc.getCustomer(), codesets));
    customerWithContactsJson.setRoleType(cwc.getRoleType());
    return customerWithContactsJson;
  }

  public List<CustomerWithContacts> createWithContactsModel(List<CustomerWithContactsJson> customersWithContactsJson) {
    List<CustomerWithContacts> customerWithContacts = new ArrayList<>();
    if (customersWithContactsJson != null) {
      customersWithContactsJson.forEach(cwcJson -> {
        customerWithContacts.add(createSingleCustomerWithContactsModel(cwcJson));
      });
    }
    return customerWithContacts;
  }

  public CustomerWithContacts createSingleCustomerWithContactsModel(CustomerWithContactsJson cwcJson) {
    return cwcJson != null ?
        new CustomerWithContacts(
            cwcJson.getRoleType(),
            createCustomerModel(cwcJson.getCustomer()),
            cwcJson.getContacts().stream().map(cJson -> createContactModel(cJson)).collect(Collectors.toList())) : null;
  }

  public CustomerWithContactsJson createCustomerWithContactsJson(CustomerRoleType roleType, CreateCustomerWithContactsJson customerWithContacts) {
    CustomerWithContactsJson customerWithContactsJson = new CustomerWithContactsJson(roleType, new CustomerJson(customerWithContacts.getCustomerId()));
    customerWithContactsJson.setContacts(customerWithContacts.getContactIds().stream()
      .map(ContactJson::new).collect(Collectors.toList()));
    return customerWithContactsJson;
  }

  static final String SSN_REPLACEMENT = "***********";

  private String getVisibleRegistryKey(CustomerType type, String registryKey) {
    if (!userCanSeeSsn() && CustomerType.PERSON.equals(type)) {
      return SSN_REPLACEMENT;
    } else {
      return registryKey;
    }
  }

  private boolean userCanSeeSsn() {
    UserJson user = userService.getCurrentUser();
    return user.getAssignedRoles().stream().anyMatch(canSeeSsn::contains);
  }
}