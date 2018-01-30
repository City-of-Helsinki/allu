package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.types.DefaultTextType;
import fi.hel.allu.common.types.EventNature;
import fi.hel.allu.pdf.domain.CableInfoTexts;
import fi.hel.allu.pdf.domain.DecisionJson;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.*;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DecisionService {

  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(DecisionService.class);

  private static final FixedLocationJson BAD_LOCATION;
  private static final String ADDRESS_LINE_SEPARATOR = "; ";
  private static final Map<DefaultTextType, String> defaultTextTypeTranslations;

  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;
  private LocationService locationService;
  private CustomerService customerService;
  private ContactService contactService;
  private ApplicationServiceComposer applicationServiceComposer;
  private final ZoneId zoneId;
  private final Locale locale;
  private final DateTimeFormatter dateTimeFormatter;
  private final DateTimeFormatter timeStampFormatter;

  static {
    BAD_LOCATION = new FixedLocationJson();
    BAD_LOCATION.setArea("Tuntematon alue");
    Map<DefaultTextType, String> tempMap = new HashMap<>();
    tempMap.put(DefaultTextType.TELECOMMUNICATION, "Tietoliikenne");
    tempMap.put(DefaultTextType.ELECTRICITY, "Sähkö");
    tempMap.put(DefaultTextType.WATER_AND_SEWAGE, "Vesi ja viemäri");
    tempMap.put(DefaultTextType.DISTRICT_HEATING_COOLING, "Kaukolämpö/jäähdytys");
    tempMap.put(DefaultTextType.GAS, "Kaasu");
    tempMap.put(DefaultTextType.UNDERGROUND_STRUCTURE, "Maanalainen rakenne/tila");
    tempMap.put(DefaultTextType.TRAMWAY, "Raitiotie");
    tempMap.put(DefaultTextType.STREET_HEATING, "Katulämmitys");
    tempMap.put(DefaultTextType.SEWAGE_PIPE, "Jäteputki");
    tempMap.put(DefaultTextType.GEOTHERMAL_WELL, "Maalämpökaivo");
    tempMap.put(DefaultTextType.GEOTECHNICAL_OBSERVATION_POST, "Geotekninen tarkkailupiste");
    tempMap.put(DefaultTextType.OTHER, "Yleisesti/muut");
    defaultTextTypeTranslations = Collections.unmodifiableMap(tempMap);
  }

  @Autowired
  public DecisionService(
      ApplicationProperties applicationProperties,
      RestTemplate restTemplate,
      LocationService locationService,
      ApplicationServiceComposer applicationServiceComposer,
      CustomerService customerService,
      ContactService contactService) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.locationService = locationService;
    this.applicationServiceComposer = applicationServiceComposer;
    this.customerService = customerService;
    this.contactService = contactService;
    zoneId = ZoneId.of("Europe/Helsinki");
    locale = new Locale("fi", "FI");
    dateTimeFormatter = DateTimeFormatter.ofPattern("d.M.uuuu");
    timeStampFormatter = DateTimeFormatter.ofPattern("d.M.uuuu 'kello' HH.mm");
  }

  /**
   * Generate the decision PDF for given application and save it to model
   * service
   *
   * @param applicationId
   *          the application's ID
   * @throws IOException
   *           when model-service responds with error
   */
  public void generateDecision(int applicationId, ApplicationJson application) throws IOException {
    DecisionJson decisionJson = new DecisionJson();
    fillJson(decisionJson, application);
    byte[] pdfData = restTemplate.postForObject(
        applicationProperties.getPdfServiceUrl(ApplicationProperties.PATH_PDF_GENERATE), decisionJson, byte[].class,
        styleSheetName(application));
    // Store the generated PDF to model:
    MultiValueMap<String, Object> requestParts = new LinkedMultiValueMap<>();
    requestParts.add("file", new ByteArrayResource(pdfData) {
      @Override // return some filename so that Spring handles this as file
      public String getFilename() {
        return "file.pdf";
      }
    });
    HttpHeaders requestHeader = new HttpHeaders();
    requestHeader.setContentType(MediaType.MULTIPART_FORM_DATA);
    HttpEntity<?> requestEntity = new HttpEntity<>(requestParts, requestHeader);
    // ...then execute the request
    ResponseEntity<String> response = restTemplate.exchange(
        applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_DECISION_STORE), HttpMethod.POST,
        requestEntity, String.class, applicationId);
    if (!response.getStatusCode().is2xxSuccessful()) {
      throw new IOException(response.getBody());
    }
  }

  /**
   * Get the decision PDF for given application from the model service
   *
   * @param applicationId
   *          the application's ID
   * @return PDF data
   */
  public byte[] getDecision(int applicationId) {
    return restTemplate.getForObject(
        applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_DECISION_GET), byte[].class,
        applicationId);
  }

  /**
   * Get the decision preview PDF for given application from the model service
   *
   * @param application the application data whose PDF preview is created.
   * @return PDF data
   */
  public byte[] getDecisionPreview(ApplicationJson application) {
    DecisionJson decisionJson = new DecisionJson();
    fillJson(decisionJson, application);
    decisionJson.setDraft(true);
    return restTemplate.postForObject(applicationProperties.getPdfServiceUrl(ApplicationProperties.PATH_PDF_GENERATE),
        decisionJson, byte[].class, styleSheetName(application));
  }

  private void fillJson(DecisionJson decisionJson, ApplicationJson application) {
    decisionJson.setEventName(application.getName());
    decisionJson.setDecisionId(application.getApplicationId());
    decisionJson.setCustomerAddressLines(customerAddressLines(application));
    decisionJson.setCustomerContactLines(customerContactLines(application));
    decisionJson.setSiteAddressLine(siteAddressLine(application));
    if (application.getLocations() != null) {
      decisionJson.setSiteArea(String.format("%.0f", Math.ceil(application.getLocations().stream().mapToDouble(l -> l.getArea()).sum())));
    }
    if (application.getType() == null) {
      throw new IllegalArgumentException("Application type is required");
    }
    switch (application.getType()) {
    case EVENT:
      fillEventSpecifics(decisionJson, application.getExtension());
      break;
    case SHORT_TERM_RENTAL:
      fillShortTermRentalSpecifics(decisionJson, application.getExtension());
      break;
    case CABLE_REPORT:
      fillCableReportSpecifics(decisionJson, application);
      break;
    default:
      break;
    }
    UserJson handler = application.getHandler();
    if (handler != null) {
      decisionJson.setHandlerTitle(handler.getTitle());
      decisionJson.setHandlerName(handler.getRealName());
    }
    List<AttachmentInfoJson> attachments = application.getAttachmentList();
    if (attachments != null) {
      decisionJson.setAttachmentNames(attachments.stream().map(a -> a.getDescription()).collect(Collectors.toList()));
    }
    decisionJson.setReservationStartDate(formatDateWithDelta(application.getStartTime(), 0));
    decisionJson.setReservationEndDate(formatDateWithDelta(application.getEndTime(), 0));
    decisionJson.setNumReservationDays(daysBetween(application.getStartTime(), application.getEndTime()) + 1);
    String additionalInfos = String.join("; ",
        streamFor(application.getLocations()).map(LocationJson::getAdditionalInfo).filter(p -> p != null)
            .map(p -> p.trim()).filter(p -> !p.isEmpty()).collect(Collectors.toList()));
    decisionJson.setSiteAdditionalInfo(additionalInfos);
    decisionJson.setDecisionDate(
        Optional.ofNullable(application.getDecisionTime()).map(dt -> formatDateWithDelta(dt, 0)).orElse("[Päätöspvm]"));
    decisionJson.setVatPercentage(99);
    decisionJson.setAdditionalConditions("[Ehtokentän teksti]");
    decisionJson.setDecisionTimestamp(ZonedDateTime.now().withZoneSameInstant(zoneId).format(timeStampFormatter));
    UserJson decider = application.getDecisionMaker();
    if (decider != null) {
      decisionJson.setDeciderTitle(decider.getTitle());
      decisionJson.setDeciderName(decider.getRealName());
    }
    decisionJson.setAppealInstructions("[Muutoksenhakuohjeet]");
    decisionJson.setPriceReason(application.getNotBillableReason());
    Integer priceInCents = application.getCalculatedPrice();
    if (priceInCents != null) {
      NumberFormat decimalFormat = NumberFormat.getCurrencyInstance(locale);
      decisionJson.setTotalRent(decimalFormat.format(priceInCents / 100.0));
      decisionJson.setSeparateBill(priceInCents > 0);
    }

  }

  private void fillShortTermRentalSpecifics(DecisionJson decisionJson, ApplicationExtensionJson extension) {
    ShortTermRentalJson strj = (ShortTermRentalJson) extension;
    if (strj != null) {
      decisionJson.setEventNature("[Tapahtuman tyyppi]");
      decisionJson.setEventDescription("[Tapahtuman kuvaus]");
      decisionJson.setEventUrl("[Tapahtuman kotisivu]");
      decisionJson.setPriceReason("[Hinnan peruste]");
    }
  }

  private void fillEventSpecifics(DecisionJson decisionJson, ApplicationExtensionJson extension) {
    EventJson ej = (EventJson) extension;
    if (ej != null) {
      decisionJson.setEventStartDate(formatDateWithDelta(ej.getEventStartTime(), 0));
      decisionJson.setEventEndDate(formatDateWithDelta(ej.getEventEndTime(), 0));
      decisionJson.setNumEventDays(daysBetween(ej.getEventStartTime(), ej.getEventEndTime()) + 1);
      decisionJson.setBuildStartDate(formatDateWithDelta(ej.getStructureStartTime(), 0));
      decisionJson.setBuildEndDate(formatDateWithDelta(ej.getEventStartTime(), -1));
      decisionJson.setTeardownStartDate(formatDateWithDelta(ej.getEventEndTime(), 1));
      decisionJson.setTeardownEndDate(formatDateWithDelta(ej.getStructureEndTime(), 0));

      decisionJson.setNumBuildAndTeardownDays(daysBetween(ej.getStructureStartTime(), ej.getEventStartTime())
          + daysBetween(ej.getEventEndTime(), ej.getStructureEndTime()));
      decisionJson.setReservationTimeExceptions(ej.getTimeExceptions());
      decisionJson.setEventDescription(ej.getDescription());
      decisionJson.setStructureArea(String.format("%.0f", ej.getStructureArea()));
      decisionJson.setStructureDescription(ej.getStructureDescription());
      decisionJson.setEventUrl(ej.getUrl());
      decisionJson.setHasEkokompassi(ej.isEcoCompass());
      decisionJson.setEventNature(eventNature(ej.getNature()));
    }
  }

  private void fillCableReportSpecifics(DecisionJson decisionJson, ApplicationJson applicationJson) {
    CableReportJson cableReportJson = (CableReportJson) applicationJson.getExtension();
    if (cableReportJson != null) {
      cableReportJson.setValidityTime(ZonedDateTime.now().plusMonths(1));
      decisionJson.setCableReportValidUntil(formatDateWithDelta(cableReportJson.getValidityTime(), 0));
      decisionJson.setWorkDescription(cableReportJson.getWorkDescription());
      applicationServiceComposer.updateApplication(applicationJson.getId(), applicationJson);
      decisionJson.setCableInfoEntries(cableReportJson.getInfoEntries().stream()
          .map(i -> new CableInfoTexts(defaultTextTypeTranslations.get(i.getType()), i.getAdditionalInfo()))
          .collect(Collectors.toList()));
      decisionJson.setMapExtractCount(Optional.ofNullable(cableReportJson.getMapExtractCount()).orElse(0));
    }
    // Override customer contact & address lines
    Optional<Pair<CustomerJson, ContactJson>> orderer = cableReportOrderer(applicationJson);

    decisionJson.setCustomerContactLines(cableReportContactLines(orderer));
    decisionJson.setCableReportOrderer(
        orderer.map(e -> e.getValue().getName()).orElse("[Johtoselvityksen tilaajan nimi puuttuu]"));
    decisionJson.setCustomerAddressLines(cableReportAddressLines(applicationJson));
  }

  /*
   * Helper to create streams for possibly null collections
   */
  private static <T> Stream<T> streamFor(Collection<T> coll) {
    return Optional.ofNullable(coll).orElse(Collections.emptyList()).stream();
  }

  /* Find the customer and contact that ordered the application */
  private Optional<Pair<CustomerJson, ContactJson>> cableReportOrderer(
      ApplicationJson applicationJson) {
    CableReportJson cableReport = (CableReportJson)applicationJson.getExtension();
    return Optional.ofNullable(cableReport.getOrderer())
        .map(id -> contactService.findById(id))
        .map(contact -> Pair.of(customerService.findCustomerById(contact.getCustomerId()), contact));
  }
  /*
   * Find the customer and contact that left the cable report and return them
   */
  private List<String> cableReportContactLines(Optional<Pair<CustomerJson, ContactJson>> orderer) {
    if (!orderer.isPresent()) {
      return Collections.singletonList("[Tilaajatieto puuttuu]");
    }
    final CustomerJson customer = orderer.get().getKey();
    final ContactJson contact = orderer.get().getValue();
    return Arrays.asList(
        customer.getName(), contact.getName(), contact.getPhone(), contact.getEmail())
        .stream().filter(p -> p != null && !p.trim().isEmpty()).collect(Collectors.toList());
  }

  /*
   * For cable reports, the customer address data should come from the customer
   * that is doing the work
   */
  private List<String> cableReportAddressLines(ApplicationJson applicationJson) {
    CustomerWithContactsJson contractor = streamFor(applicationJson.getCustomersWithContacts())
        .filter(cwc -> CustomerRoleType.CONTRACTOR.equals(cwc.getRoleType())).findFirst().orElse(null);
    if (contractor == null) {
      return Collections.singletonList("[Kaivajan tiedot puuttuvat]");
    }
    final CustomerJson customer = contractor.getCustomer();
    return Arrays
        .asList(customer.getName(), customer.getPostalAddress().getStreetAddress(),
            customer.getPostalAddress().getCity(), customer.getPhone())
        .stream().filter(p -> p != null && !p.trim().isEmpty()).collect(Collectors.toList());

  }
  private String formatDateWithDelta(ZonedDateTime zonedDateTime, int deltaDays) {
    if (zonedDateTime == null) {
      return null;
    }
    return zonedDateTime.plusDays(deltaDays).withZoneSameInstant(zoneId)
        .format(dateTimeFormatter);
  }

  private List<String> customerAddressLines(ApplicationJson applicationJson) {
    // return lines in format {"[Customer name], [SSID]", "[address, Postal
    // code + city]",
    // "[email, phone]"}
    // TODO: perhaps this should work with other than APPLICANT roles too?
    Optional<CustomerWithContactsJson> cwcOpt =
        applicationJson.getCustomersWithContacts().stream().filter(cwc -> CustomerRoleType.APPLICANT.equals(cwc.getRoleType())).findFirst();

    final List<String> addressLines = new ArrayList<>();
    cwcOpt.ifPresent(cwc -> {
      addressLines.addAll(
          Arrays.asList(
              String.format("%s, %s", cwc.getCustomer().getName(), cwc.getCustomer().getRegistryKey()),
              postalAddress(cwc.getCustomer().getPostalAddress()),
              String.format("%s, %s", cwc.getCustomer().getEmail(), cwc.getCustomer().getPhone())));
    });
    return addressLines;
  }

  private List<String> customerContactLines(ApplicationJson application) {
    // returns {"[Yhteyshenkilön nimi]", "[Sähköpostiosoite, puhelin]"}

    Optional<CustomerWithContactsJson> cwcOpt =
        application.getCustomersWithContacts().stream().filter(cwc -> CustomerRoleType.APPLICANT.equals(cwc.getRoleType())).findFirst();
    final List<String> contactLines = new ArrayList<>();
    cwcOpt.ifPresent(cwc -> {
      contactLines.addAll(
          cwc.getContacts().stream()
          .flatMap(c ->
              Stream.of(cwc.getCustomer().getName(), String.format("%s, %s", cwc.getCustomer().getEmail(), cwc.getCustomer().getPhone())))
          .collect(Collectors.toList()));
    });
    return contactLines;
  }

  private String siteAddressLine(ApplicationJson application) {
    List<Integer> locationIds = null;
    if (application.getLocations() != null) {
      locationIds = application.getLocations().stream()
          .filter(l -> l.getFixedLocationIds() != null)
          .flatMap(l -> l.getFixedLocationIds().stream())
          .collect(Collectors.toList());
    }

    StringBuilder sb = new StringBuilder();
    if (locationIds != null && !locationIds.isEmpty()) {
      sb.append(fixedLocationAddressLine(locationIds, application.getKind()));
    }
    if (application.getLocations() != null && application.getLocations().size() > 0) {
      if (sb.length() != 0) {
        sb.append(ADDRESS_LINE_SEPARATOR);
      }
      // return comma separated street address list
      sb.append(application.getLocations().stream()
          .filter(l -> l.getPostalAddress() != null)
          .map(l -> l.getPostalAddress().getStreetAddress())
          .filter(s -> s != null)
          .collect(Collectors.joining(ADDRESS_LINE_SEPARATOR)));
    }

    return sb.toString();
  }

  private String fixedLocationAddressLine(List<Integer> locationIds, ApplicationKind applicationKind) {
    // Get all defined fixed outdoor event locations from locationService:
    List<FixedLocationJson> allFixedLocations = locationService.getFixedLocationList().stream()
        .filter(fl -> fl.getApplicationKind() == applicationKind).collect(Collectors.toList());
    // Create lookup map id -> fixed location:
    Map<Integer, FixedLocationJson> flMap = allFixedLocations.stream()
        .collect(Collectors.toMap(FixedLocationJson::getId, Function.identity()));
    // Find all fixed locations that match locationIds and group them by area
    // name:
    Map<String, List<FixedLocationJson>> grouped = locationIds.stream().map(id -> flMap.get(id))
        .filter(fl -> fl != null)
        .collect(Collectors.groupingBy(FixedLocationJson::getArea));
    if (grouped.isEmpty()) {
      return BAD_LOCATION.getArea();
    }
    // Now generate the line for each area:
    StringBuilder addressLine = new StringBuilder();
    for (Map.Entry<String, List<FixedLocationJson>> entry : grouped.entrySet()) {
      if (addressLine.length() != 0) {
        addressLine.append(ADDRESS_LINE_SEPARATOR);
      }
      addressLine.append(addressLineFor(entry.getValue()));
    }
    return addressLine.toString();
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
   * Return address like "Mannerheimintie 3, 00100 Helsinki", skip null/empty
   * values
   */
  private String postalAddress(PostalAddressJson a) {
    Optional<String> street = Optional.ofNullable(a.getStreetAddress()).filter(s -> !s.isEmpty());
    Optional<String> postal = Optional.ofNullable(a.getPostalCode()).filter(s -> !s.isEmpty());
    Optional<String> city = Optional.ofNullable(a.getCity()).filter(s -> !s.isEmpty());
    StringBuilder sb = new StringBuilder();
    if (street.isPresent()) {
      sb.append(street.get());
      if (postal.isPresent() || city.isPresent()) {
        sb.append(", ");
      }
    }
    if (postal.isPresent()) {
      sb.append(postal.get());
      if (city.isPresent()) {
        sb.append(" ");
      }
    }
    if (city.isPresent()) {
      sb.append(city.get());
    }
    return sb.toString();
  }

  private String eventNature(EventNature nature) {
    switch (nature) {
    case CLOSED:
      return "Kutsuvierastilaisuus tai muu vastaava suljettu tapahtuma";
    case PUBLIC_FREE:
      return "Yleisölle pääsymaksuton tapahtuma";
    case PUBLIC_NONFREE:
      return "Yleisölle pääsymaksullinen tapahtuma";
    default:
      return "***TUNTEMATON TAPAHTUMALUONNE***";
    }
  }

  private int daysBetween(ZonedDateTime startDate, ZonedDateTime endDateExclusive) {
    if (startDate == null || endDateExclusive == null) {
      return 0;
    }
    return (int) (startDate.until(endDateExclusive, ChronoUnit.DAYS));
  }

  // Get the stylesheet name to use for given application.
  private String styleSheetName(ApplicationJson application) {
    /*
     * FIXME: only EVENT, SHORT_TERM_RENTAL, and CABLE_REPORT are supported. For
     * others, use "DUMMY"
     */
    if (application.getType() == ApplicationType.EVENT
        || application.getType() == ApplicationType.SHORT_TERM_RENTAL
        || application.getType() == ApplicationType.CABLE_REPORT) {
      return application.getType().name();
    }
    return "DUMMY";
  }
}
