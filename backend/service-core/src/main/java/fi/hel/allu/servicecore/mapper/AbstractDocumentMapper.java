package fi.hel.allu.servicecore.mapper;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.servicecore.service.ContactService;
import fi.hel.allu.servicecore.service.CustomerService;
import fi.hel.allu.servicecore.service.LocationService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractDocumentMapper<T> {
  protected static final Logger logger = LoggerFactory.getLogger(AbstractDocumentMapper.class);
  protected final CustomerService customerService;
  protected final ContactService contactService;
  protected final LocationService locationService;

  private static final String ADDRESS_LINE_SEPARATOR = "; ";
  private static final String CITY_DISTRICT_SEPARATOR = ", ";
  private static final String UNKNOWN_ADDRESS = "[Osoite ei tiedossa]";
  private static final FixedLocationJson BAD_LOCATION = new FixedLocationJson();

  static {
    BAD_LOCATION.setArea("Tuntematon alue");
  }


  public AbstractDocumentMapper(CustomerService customerService,
      ContactService contactService, LocationService locationService) {
    this.customerService = customerService;
    this.contactService = contactService;
    this.locationService = locationService;
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
    Optional<CustomerJson> customer = getCustomerByRole(application, roleType)
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
    return getCustomerByRole(application, roleType)
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
        .filter(s -> !StringUtils.isEmpty(s))
        .collect(Collectors.joining(" "));
    return Arrays.asList(a.getStreetAddress(), postalCodeAndCity).stream()
        .filter(s -> !StringUtils.isEmpty(s))
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

  protected String siteAddressLine(ApplicationJson application) {
    if (!hasLocations(application)) {
      return "";
    }
    final Map<Integer, FixedLocationJson> fixedLocationsById = fetchFixedLocations(application);
    return application.getLocations().stream().map(l -> locationAddress(l, fixedLocationsById))
        .collect(Collectors.joining(ADDRESS_LINE_SEPARATOR));
  }

  protected String siteCityDistrict(ApplicationJson application) {
    if (!hasLocations(application)) {
      return "";
    }
    final List<CityDistrictInfoJson> cityDistricts = locationService.getCityDistrictList();
    return application.getLocations().stream().map(l -> getCityDistrict(l, cityDistricts))
        .collect(Collectors.joining(CITY_DISTRICT_SEPARATOR));
  }

  private String getCityDistrict(LocationJson location, List<CityDistrictInfoJson> cityDistricts) {
    final Integer cityDistrictId = getCityDistrictId(location);
    if (cityDistrictId != null) {
      final Optional<CityDistrictInfoJson> cityDistrictInfo = getCityDistrictInfo(cityDistrictId, cityDistricts);
      return cityDistrictInfo.map(c -> c.getName()).orElse("");
    }
    return "";
  }

  private Integer getCityDistrictId(LocationJson locationJson) {
    return Optional.ofNullable(locationJson.getCityDistrictIdOverride()).orElse(locationJson.getCityDistrictId());
  }

  private Optional<CityDistrictInfoJson> getCityDistrictInfo(Integer cityDistrictId, List<CityDistrictInfoJson> cityDistricts) {
    return cityDistricts.stream().filter(c -> c.getId().equals(cityDistrictId)).findAny();
  }

  /*
   * If the application references any fixed locations, fetches all fixed
   * locations that match the application's kind and creates a lookup map.
   * Otherwise, just returns an empty map.
   */
  Map<Integer, FixedLocationJson> fetchFixedLocations(ApplicationJson applicationJson) {
    if (applicationJson.getLocations().stream().map(l -> l.getFixedLocationIds())
        .allMatch(flIds -> CollectionUtils.isEmpty(flIds))) {
      return Collections.emptyMap();
    } else {
      final ApplicationKind applicationKind = applicationJson.getKind();
      return locationService.getAllFixedLocations().stream()
          .filter(fl -> fl.getApplicationKind() == applicationKind)
          .collect(Collectors.toMap(FixedLocationJson::getId, Function.identity()));
    }
  }

  /*
   * Generate a location address text. If the location refers to fixed
   * locations, create the string "[Fixed location name], [Segments]". Otherwise
   * use location's postal address.
   */
  private String locationAddress(LocationJson locationJson, Map<Integer, FixedLocationJson> fixedLocationsById) {
    if (!CollectionUtils.isEmpty(locationJson.getFixedLocationIds())) {
      return fixedLocationAddresses(locationJson.getFixedLocationIds(), fixedLocationsById);
    } else {
      return Optional.ofNullable(locationJson.getPostalAddress()).map(pa -> postalAddress(pa)).orElse(UNKNOWN_ADDRESS);
    }
  }

  /*
   * Given a list of fixed locations, return an address line
   */
  private String fixedLocationAddresses(List<Integer> fixedLocationIds,
                                        Map<Integer, FixedLocationJson> fixedLocationsById) {
    Map<String, List<FixedLocationJson>> grouped = fixedLocationIds.stream()
        .map(id -> fixedLocationsById.get(id))
        .filter(fl -> fl != null)
        .collect(Collectors.groupingBy(FixedLocationJson::getArea));
    if (grouped.isEmpty()) {
      return BAD_LOCATION.getArea();
    }
    return grouped.entrySet().stream().map(es -> addressLineFor(es.getValue()))
        .collect(Collectors.joining(ADDRESS_LINE_SEPARATOR));
  }


  // Generate a line like "Rautatientori, lohkot A, C, D".
  // all locations are from same area, there is always at least one location.
  private CharSequence addressLineFor(List<FixedLocationJson> locations) {
    // Start with area name (e.g., "Rautatientori"):
    StringBuilder line = new StringBuilder(locations.get(0).getArea());
    String firstSection = locations.get(0).getSection();
    if (locations.size() == 1) {
      // Only one section and could be nameless:
      if (firstSection != null) {
        line.append(", lohko " + firstSection);
      }
    } else {
      // Many sections, so they all have names
      line.append(", lohkot " + firstSection);
      for (int i = 1; i < locations.size(); ++i) {
        line.append(String.format(", %s", locations.get(i).getSection()));
      }
    }
    return line;
  }

  /*
   * Helper to create streams for possibly null collections
   */
  protected static <T> Stream<T> streamFor(Collection<T> coll) {
    return Optional.ofNullable(coll).orElse(Collections.emptyList()).stream();
  }

  private boolean hasLocations(ApplicationJson application) {
    return !CollectionUtils.isEmpty(application.getLocations());
  }

  protected Optional<CustomerWithContactsJson> getCustomerByRole(ApplicationJson application, CustomerRoleType role) {
    return application.getCustomersWithContacts().stream()
        .filter(cwc -> role.equals(cwc.getRoleType()))
        .findFirst();
  }
}
