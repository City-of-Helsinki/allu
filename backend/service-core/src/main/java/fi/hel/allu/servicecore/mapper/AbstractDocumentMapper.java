package fi.hel.allu.servicecore.mapper;

import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.servicecore.service.ContactService;
import fi.hel.allu.servicecore.service.CustomerService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractDocumentMapper<T> {
  protected static final Logger logger = LoggerFactory.getLogger(AbstractDocumentMapper.class);
  protected final CustomerService customerService;
  protected final ContactService contactService;


  public AbstractDocumentMapper(CustomerService customerService,
                                ContactService contactService) {
    this.customerService = customerService;
    this.contactService = contactService;

  }

  protected List<String> customerAddressLines(ApplicationJson applicationJson) {
    return addressLines(applicationJson, CustomerRoleType.APPLICANT);
  }

  protected List<String> customerContactLines(ApplicationJson application) {
    return contactLines(application, CustomerRoleType.APPLICANT);
  }

  protected List<String> addressLines(ApplicationJson application, CustomerRoleType roleType) {
    // return lines in format {"[Customer name], [SSID]", "[address, Postal
    // code + city]",
    // "[email, phone]"}
    Optional<CustomerJson> customer = application.getCustomersWithContacts().stream()
        .filter(cwc -> roleType.equals(cwc.getRoleType()))
        .findFirst()
        .map(cwc -> cwc.getCustomer());

    return addressLines(customer);
  }

  protected List<String> addressLines(Optional<CustomerJson> customer) {
    return customer
        .map(c -> Arrays.asList(
            combinePossibleBlankStrings(c.getName(), getCustomerRegistryKey(c)),
            postalAddress(c.getPostalAddress()),
            combinePossibleBlankStrings(c.getEmail(), c.getPhone()))
        ).orElse(Collections.emptyList());
  }

  protected List<String> contactLines(ApplicationJson application, CustomerRoleType roleType) {
    // [Yhteyshenkilön nimi]", "[Sähköpostiosoite, puhelin]
    return application.getCustomersWithContacts().stream()
        .filter(cwc -> roleType.equals(cwc.getRoleType()))
        .findFirst()
        .map(cwc -> contactLines(cwc.getContacts()))
        .orElse(Collections.emptyList());
  }

  private List<String> contactLines(List<ContactJson> contacts) {
    return contacts.stream()
        .flatMap(c -> Stream.of(c.getName(), combinePossibleBlankStrings(c.getEmail(), c.getPhone())))
        .collect(Collectors.toList());
  }

  private String combinePossibleBlankStrings(String first, String second) {
    if (StringUtils.isBlank(first) && StringUtils.isBlank(second)) {
      return "";
    }
    if (StringUtils.isBlank(first)) {
      return second;
    }
    if (StringUtils.isBlank(second)) {
      return first;
    }
    return String.format("%s, %s", first, second);
  }

  private String getCustomerRegistryKey(CustomerJson customer) {
    // Social security number (hetu) must never be shown on a decision
    if (customer.getType() == CustomerType.PERSON) {
      return null;
    }
    return customer.getRegistryKey();
  }

  /*
   * Return address like "Mannerheimintie 3, 00100 Helsinki", skip null/empty
   * values
   */
  protected String postalAddress(PostalAddressJson a) {
    final String postalCodeAndCity = Arrays.asList(a.getPostalCode(), a.getCity()).stream()
        .filter(s -> s != null && !s.isEmpty())
        .collect(Collectors.joining(" "));
    return Arrays.asList(a.getStreetAddress(), postalCodeAndCity).stream()
        .filter(s -> s != null && !s.isEmpty())
        .collect(Collectors.joining(", "));
  }

  protected void convertNonBreakingSpacesToSpaces(Object document) {
    final Field[] fields = document.getClass().getDeclaredFields();
    for (Field field : fields) {
      if (field.getType().equals(String.class)) {
        try {
          final boolean accessible = field.isAccessible();
          field.setAccessible(true);
          String value = (String)field.get(document);
          value = convertNonBreakingSpaceToSpace(value);
          field.set(document, value);
          field.setAccessible(accessible);
        } catch (IllegalArgumentException | IllegalAccessException e) {
          logger.error("Error while converting non-breaking spaces", e);
        }
      } else if (field.getType().equals(List.class)) {
        final Type genericFieldType = field.getGenericType();
        if (genericFieldType instanceof ParameterizedType){
          final ParameterizedType aType = (ParameterizedType)genericFieldType;
          final Type[] fieldArgTypes = aType.getActualTypeArguments();
          if (fieldArgTypes.length == 1) {
            final Class fieldArgClass = (Class)fieldArgTypes[0];
            if (fieldArgClass.equals(String.class)) {
              try {
                final boolean accessible = field.isAccessible();
                field.setAccessible(true);
                final List<String> values = (List<String>)field.get(document);
                if (values != null) {
                  final List<String> newValues = new ArrayList<>();
                  for (String value : values) {
                    newValues.add(convertNonBreakingSpaceToSpace(value));
                  }
                  field.set(document, newValues);
                  field.setAccessible(accessible);
                }
              } catch (IllegalArgumentException | IllegalAccessException e) {
                logger.error("Error while converting non-breaking spaces", e);
              }
            }
          }
        }
      }
    }
  }

  private String convertNonBreakingSpaceToSpace(String value) {
    if (value != null) {
      return value.replace('\u00A0',' ');
    } else {
      return null;
    }
  }

  /*
   * Split the given string into a list of strings. For empty Optional, give
   * empty list.
   */
  protected List<String> splitToList(Optional<String> string) {
    return string.map(s -> s.split("\n"))
        .map(a -> Arrays.stream(a)).map(s -> s.collect(Collectors.toList()))
        .orElse(Collections.emptyList());
  }
}
