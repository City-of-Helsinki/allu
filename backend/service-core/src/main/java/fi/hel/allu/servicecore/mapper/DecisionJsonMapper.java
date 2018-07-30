package fi.hel.allu.servicecore.mapper;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.hel.allu.common.domain.types.*;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.types.DefaultTextType;
import fi.hel.allu.common.types.EventNature;
import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.model.domain.util.EventDayUtil;
import fi.hel.allu.pdf.domain.CableInfoTexts;
import fi.hel.allu.pdf.domain.ChargeInfoTexts;
import fi.hel.allu.pdf.domain.DecisionJson;
import fi.hel.allu.pdf.domain.KindWithSpecifiers;
import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.servicecore.service.*;

@Component
public class DecisionJsonMapper {

  private static final Logger logger = LoggerFactory.getLogger(DecisionJsonMapper.class);

  private static final String ADDRESS_LINE_SEPARATOR = "; ";
  private static final Map<DefaultTextType, String> defaultTextTypeTranslations;
  private static final String UNKNOWN_ADDRESS = "[Osoite ei tiedossa]";
  private static final FixedLocationJson BAD_LOCATION;

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

  private final NumberFormat currencyFormat;
  private final ZoneId zoneId;
  private final Locale locale;

  private final LocationService locationService;
  private final CustomerService customerService;
  private final ContactService contactService;
  private final MetaService metaService;
  private final ChargeBasisService chargeBasisService;
  private final DateTimeFormatter dateTimeFormatter;
  private final DateTimeFormatter timeStampFormatter;
  private final DecimalFormat decimalFormat;

  @Autowired
  public DecisionJsonMapper(LocationService locationService,
      ApplicationServiceComposer applicationServiceComposer,
      CustomerService customerService,
      ContactService contactService,
      ChargeBasisService chargeBasisService,
      MetaService metaService) {
    this.locationService = locationService;
    this.customerService = customerService;
    this.contactService = contactService;
    this.chargeBasisService = chargeBasisService;
    this.metaService = metaService;
    dateTimeFormatter = DateTimeFormatter.ofPattern("d.M.uuuu");
    timeStampFormatter = DateTimeFormatter.ofPattern("d.M.uuuu 'kello' HH.mm");
    decimalFormat = new DecimalFormat("0.##");
    locale = new Locale("fi", "FI");
    zoneId = ZoneId.of("Europe/Helsinki");
    currencyFormat = NumberFormat.getCurrencyInstance(locale);
  }


  public DecisionJson mapDecisionJson(ApplicationJson application, boolean draft) {
    DecisionJson decisionJson = new DecisionJson();
    decisionJson.setDraft(draft);
    decisionJson.setEventName(application.getName());
    decisionJson.setDecisionId(application.getApplicationId());
    decisionJson.setCustomerAddressLines(customerAddressLines(application));
    decisionJson.setCustomerContactLines(customerContactLines(application));
    decisionJson.setApplicantName(applicantName(application));
    decisionJson.setSiteAddressLine(siteAddressLine(application));
    final Map<ApplicationKind, List<ApplicationSpecifier>> applicationsKindsWithSpecifiers = application.getKindsWithSpecifiers();
    final List<KindWithSpecifiers> kindsWithSpecifiers = new ArrayList<>();
    applicationsKindsWithSpecifiers.keySet().forEach((kind) -> {
      final List<String> specifiers = new ArrayList<>();
      applicationsKindsWithSpecifiers.get(kind).forEach(s -> specifiers.add(translate(s)));
      KindWithSpecifiers k = new KindWithSpecifiers();
      k.setKind(translate(kind));
      k.setSpecifiers(specifiers);
      kindsWithSpecifiers.add(k);
    });
    decisionJson.setKinds(kindsWithSpecifiers);

    getSiteArea(application.getLocations()).ifPresent(siteArea -> decisionJson.setSiteArea(siteArea));

    if (application.getType() == null) {
      throw new IllegalArgumentException("Application type is required");
    }
    switch (application.getType()) {
    case EVENT:
      fillEventSpecifics(decisionJson, application);
      break;
    case SHORT_TERM_RENTAL:
      fillShortTermRentalSpecifics(decisionJson, application);
      break;
    case CABLE_REPORT:
      fillCableReportSpecifics(decisionJson, application);
      break;
    case PLACEMENT_CONTRACT:
      fillPlacementContractSpecifics(decisionJson, application);
      break;
    case TEMPORARY_TRAFFIC_ARRANGEMENTS:
      fillTemporaryTrafficArrangementSpecifics(decisionJson, application);
    default:
      break;
    }
    UserJson handler = application.getHandler();
    if (handler != null) {
      decisionJson.setHandlerTitle(handler.getTitle());
      decisionJson.setHandlerName(handler.getRealName());
      decisionJson.setHandlerEmail(handler.getEmailAddress());
      decisionJson.setHandlerPhone(handler.getPhone());
    }
    UserJson supervisor = findSupervisor(application);
    if (supervisor != null) {
      decisionJson.setSupervisorName(supervisor.getRealName());
      decisionJson.setSupervisorEmail(supervisor.getEmailAddress());
      decisionJson.setSupervisorPhone(supervisor.getPhone());
    }
    List<AttachmentInfoJson> attachments = application.getAttachmentList();
    if (attachments != null) {
      decisionJson.setAttachmentNames(attachments.stream().filter(a -> a.isDecisionAttachment())
          .map(a -> StringUtils.defaultIfEmpty(a.getDescription(), a.getName()))
          .collect(Collectors.toList()));
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
    decisionJson.setVatPercentage(24); // FIXME: find actual value somehow
    decisionJson.setAdditionalConditions(
        splitToList(Optional.ofNullable(application.getExtension()).map(e -> e.getTerms())));
    decisionJson.setDecisionTimestamp(ZonedDateTime.now().withZoneSameInstant(zoneId).format(timeStampFormatter));
    UserJson decider = application.getDecisionMaker();
    if (decider != null) {
      decisionJson.setDeciderTitle(decider.getTitle());
      decisionJson.setDeciderName(decider.getRealName());
    }
    decisionJson.setAppealInstructions("[Muutoksenhakuohjeet]");
    decisionJson.setNotBillable(Boolean.TRUE.equals(application.getNotBillable()));
    decisionJson.setNotBillableReason(application.getNotBillableReason());
    Integer priceInCents = application.getCalculatedPrice();
    if (priceInCents != null) {
      decisionJson.setTotalRent(currencyFormat.format(priceInCents / 100.0));
      decisionJson.setSeparateBill(priceInCents > 0);
    }
    fillCargeBasisInfo(decisionJson, application);
    decisionJson.setIdentificationNumber(application.getIdentificationNumber());
    decisionJson.setReplacingDecision(application.getReplacesApplicationId() != null);
    return decisionJson;
  }

  private Optional<String> getSiteArea(List<LocationJson> locations) {
    return Optional.ofNullable(locations)
        .map(locs -> locs.stream().mapToDouble(this::getEffectiveArea).sum())
        .map(totalArea -> Math.ceil(totalArea))
        .map(totalArea -> String.format("%.0f", totalArea));
  }

  private Double getEffectiveArea(LocationJson location) {
    return Optional.ofNullable(location.getAreaOverride())
      .orElse(location.getArea());
  }

  /*
   * Split the given string into a list of strings. For empty Optional, give
   * empty list.
   */
  private List<String> splitToList(Optional<String> string) {
    return string.map(s -> s.split("\n"))
        .map(a -> Arrays.stream(a)).map(s -> s.collect(Collectors.toList()))
        .orElse(Collections.emptyList());
  }
  /*
   * Read application's charge basis entries, order them, and generate matching
   * charge info texts.
   */
  private void fillCargeBasisInfo(DecisionJson decisionJson, ApplicationJson application) {
    if (application.getId() == null) {
      return;
    }
    List<ChargeBasisEntry> chargeBasisEntries = chargeBasisService.getChargeBasis(application.getId());
    Map<String, List<ChargeBasisEntry>> entriesByReferred = chargeBasisEntries.stream()
        .collect(Collectors.groupingBy(cbe -> StringUtils.defaultString(cbe.getReferredTag())));
    List<Pair<Integer, ChargeBasisEntry>> orderedEntries = listReferringEntries(entriesByReferred, "", 0);
    decisionJson.setChargeInfoEntries(orderedEntries.stream().map(p -> new ChargeInfoTexts(p.getLeft(),
        p.getRight().getText(), p.getRight().getExplanation(),
        chargeQuantity(p.getRight()), chargeUnitPrice(p.getRight()), chargeNetPrice(p.getRight())))
        .collect(Collectors.toList()));
  }

  /*
   * What to write in the "quantity" column in charge itemization? (skipped for
   * percent entries)
   */
  private String chargeQuantity(ChargeBasisEntry e) {
    if (ChargeBasisUnit.PERCENT.equals(e.getUnit())) {
      return null;
    } else {
      return decimalFormat.format(e.getQuantity()) + unitString(e.getUnit());
    }
  }

  /*
   * How to write the charge unit in itemization
   */
  private String unitString(ChargeBasisUnit unit) {
    switch (unit) {
      case DAY:
        return " pv";
      case HOUR:
        return " t";
      case MONTH:
        return " kk";
      case PERCENT:
        return " %";
      case PIECE:
        return " kpl";
      case SQUARE_METER:
        return " m²";
      case WEEK:
        return " vko";
      case YEAR:
        return " v";
      default:
        return "";
    }
  }

  /*
   * How to write the unit price in itemization (skipped for percentage entries)
   */
  private String chargeUnitPrice(ChargeBasisEntry e) {
    if (ChargeBasisUnit.PERCENT.equals(e.getUnit())) {
      return null;
    } else {
      return "à " + currencyFormat.format(e.getUnitPrice() * 0.01);
    }
  }

  /*
   * How to write the total price in itemization
   */
  private String chargeNetPrice(ChargeBasisEntry e) {
    if (ChargeBasisUnit.PERCENT.equals(e.getUnit())) {
      return e.getQuantity() + " %";
    } else {
      return currencyFormat.format(e.getNetPrice() * 0.01);
    }
  }

  /*
   * Recursively go trough the multimap of [key, referring entries] to generate
   * the list of entries in referral order:
   *
   * EntryA
   * +--EntryB(refers A)
   *     +--- EntryD(refers B)
   * +--EntryC(refers A)
   * EntryE
   */
  private List<Pair<Integer, ChargeBasisEntry>> listReferringEntries(
      Map<String, List<ChargeBasisEntry>> entriesByReferred, String key,
      int level) {
    // Avoid infinite recursion if data has errors:
    final int RECURSION_LIMIT = 99;
    if (level > RECURSION_LIMIT) {
      return Collections.emptyList();
    }
    List<Pair<Integer, ChargeBasisEntry>> result = new ArrayList<>();
    entriesByReferred.getOrDefault(key, Collections.emptyList()).forEach(e -> {
      result.add(Pair.of(level, e));
      if (!StringUtils.isEmpty(e.getTag())) {
        result.addAll(listReferringEntries(entriesByReferred, e.getTag(), level + 1));
      }
    });
    return result;
  }

  private void fillShortTermRentalSpecifics(DecisionJson decisionJson, ApplicationJson application) {
    ShortTermRentalJson strj = (ShortTermRentalJson) application.getExtension();
    if (strj != null) {
      decisionJson
          .setEventNature("Lyhytaikainen maanvuokraus, " + translate(application.getKind()));
      decisionJson.setEventDescription(strj.getDescription());
      decisionJson.setPriceBasisText(shortTermPriceBasis(application.getKind(), strj));
    }
    if (ApplicationKind.BRIDGE_BANNER.equals(application.getKind())) {
      // For bridge banners, site area should be skipped in printout
      decisionJson.setSiteArea(null);
    }
  }

  /*
   * Return the application-kind specific price basis text for Short term
   * rentals
   */
  private String shortTermPriceBasis(ApplicationKind kind, ShortTermRentalJson shortTermRental) {
    switch (kind) {
      case BENJI:
        return "320 &euro;/p&auml;iv&auml; + alv";
      case BRIDGE_BANNER:
        if (BooleanUtils.isTrue(shortTermRental.getCommercial())) {
          return "Kaupalliset toimijat: 750 &euro;/kalenteriviikko + alv";
        } else {
          return "Ei-kaupalliset toimijat: 150 &euro;/kalenteriviikko + alv";
        }
      case CIRCUS:
        return "200 €/p&auml;iv&auml; + alv";
      case DOG_TRAINING_EVENT:
        return "<ul><li>Kertamaksu yhdistyksille: 50 &euro;/kerta + alv</li>"
            + "<li>Kertamaksu yrityksille: 100 &euro;/kerta + alv</li></ul>";
      case DOG_TRAINING_FIELD:
        return "<ul><li>Vuosivuokra yhdistyksille: 100 &euro;/vuosi</li>"
            + "<li>Vuosivuokra yrityksille: 200 &euro;/vuosi</li></ul>";
      case KESKUSKATU_SALES:
      case SEASON_SALE:
        return "50 &euro;/p&auml;iv&auml;/alkava 10m&sup2; + alv";
      case PROMOTION_OR_SALES:
        return "150 &euro;/kalenterivuosi + alv";
      case SUMMER_THEATER:
        return "120 &euro;/kuukausi n&auml;yt&auml;nt&ouml;ajalta";
      case URBAN_FARMING:
        return "2 &euro;/m&sup2;/viljelykausi";
      case OTHER:
      case ART:
      case SMALL_ART_AND_CULTURE:
      case STORAGE_AREA:
        return null;
      default:
        return "[FIXME: Perustetta ei m&auml;&auml;ritetty]";
    }
  }

  private void fillEventSpecifics(DecisionJson decisionJson, ApplicationJson application) {
    EventJson ej = (EventJson) application.getExtension();
    if (ej != null) {
      decisionJson.setEventStartDate(formatDateWithDelta(ej.getEventStartTime(), 0));
      decisionJson.setEventEndDate(formatDateWithDelta(ej.getEventEndTime(), 0));
      decisionJson.setNumEventDays(daysBetween(ej.getEventStartTime(), ej.getEventEndTime()) + 1);
      decisionJson.setBuildStartDate(formatDateWithDelta(buildTearDate(application.getStartTime(), ej.getEventStartTime()), 0));
      decisionJson.setBuildEndDate(formatDateWithDelta(ej.getEventStartTime(), -1));
      decisionJson.setTeardownStartDate(formatDateWithDelta(ej.getEventEndTime(), 1));
      decisionJson.setTeardownEndDate(formatDateWithDelta(buildTearDate(application.getEndTime(), ej.getEventEndTime()), 0));

      decisionJson.setNumBuildAndTeardownDays(EventDayUtil.buildDays(ej.getEventStartTime(), ej.getEventEndTime(),
          application.getStartTime(), application.getEndTime()));
      decisionJson.setReservationTimeExceptions(ej.getTimeExceptions());
      decisionJson.setEventDescription(ej.getDescription());
      decisionJson.setStructureArea(String.format("%.0f", ej.getStructureArea()));
      decisionJson.setStructureDescription(ej.getStructureDescription());
      decisionJson.setEventUrl(ej.getUrl());
      decisionJson.setHasEkokompassi(ej.isEcoCompass());
      decisionJson.setEventNature(eventNature(ej.getNature()));
    }
  }

  private ZonedDateTime buildTearDate(ZonedDateTime applicationDate, ZonedDateTime eventDate) {
    if (applicationDate != null && eventDate != null) {
      // build and tear dates exist when application's date differs from event's date
      return applicationDate.isEqual(eventDate) ? null : applicationDate;
    }
    return null;
  }

  private void fillCableReportSpecifics(DecisionJson decisionJson, ApplicationJson applicationJson) {
    CableReportJson cableReportJson = (CableReportJson) applicationJson.getExtension();
    if (cableReportJson != null) {
      decisionJson.setCableReportValidUntil(formatDateWithDelta(cableReportJson.getValidityTime(), 0));
      decisionJson.setWorkDescription(cableReportJson.getWorkDescription());
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

  private void fillPlacementContractSpecifics(DecisionJson decisionJson, ApplicationJson applicationJson) {
    PlacementContractJson placementContract = (PlacementContractJson)applicationJson.getExtension();
    if (placementContract != null) {
      decisionJson.setSectionNumber(placementContract.getSectionNumber());
      decisionJson.setContractText(placementContract.getContractText());
    }
  }

  private void fillTemporaryTrafficArrangementSpecifics(DecisionJson decision, ApplicationJson application) {
    TrafficArrangementJson trafficArrangement = (TrafficArrangementJson)application.getExtension();
    decision.setContractorAddressLines(addressLines(application, CustomerRoleType.CONTRACTOR));
    decision.setContractorContactLines(contactLines(application, CustomerRoleType.CONTRACTOR));
    decision.setPropertyDeveloperAddressLines(addressLines(application, CustomerRoleType.PROPERTY_DEVELOPER));
    decision.setPropertyDeveloperContactLines(contactLines(application, CustomerRoleType.PROPERTY_DEVELOPER));
    decision.setRepresentativeAddressLines(addressLines(application, CustomerRoleType.REPRESENTATIVE));
    decision.setRepresentativeContactLines(contactLines(application, CustomerRoleType.REPRESENTATIVE));
    decision.setWorkPurpose(trafficArrangement.getWorkPurpose());
    decision.setTrafficArrangements(trafficArrangement.getTrafficArrangements());
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
    return addressLines(applicationJson, CustomerRoleType.APPLICANT);
  }

  private List<String> customerContactLines(ApplicationJson application) {
    return contactLines(application, CustomerRoleType.APPLICANT);
  }

  private String applicantName(ApplicationJson applicationJson) {
    Optional<CustomerWithContactsJson> cwcOpt =
        applicationJson.getCustomersWithContacts().stream().filter(cwc -> CustomerRoleType.APPLICANT.equals(cwc.getRoleType())).findFirst();
    return cwcOpt.map(cwc -> cwc.getCustomer().getName()).orElse(null);
  }

  private List<String> addressLines(ApplicationJson application, CustomerRoleType roleType) {
    // return lines in format {"[Customer name], [SSID]", "[address, Postal
    // code + city]",
    // "[email, phone]"}
    Optional<CustomerWithContactsJson> cwcOpt =
        application.getCustomersWithContacts().stream().filter(cwc -> roleType.equals(cwc.getRoleType())).findFirst();

    final List<String> addressLines = new ArrayList<>();
    cwcOpt.ifPresent(cwc -> {
      addressLines.addAll(
          Arrays.asList(
              combinePossibleBlankStrings(cwc.getCustomer().getName(), getCustomerRegistryKey(cwc.getCustomer())),
              postalAddress(cwc.getCustomer().getPostalAddress()),
              combinePossibleBlankStrings(cwc.getCustomer().getEmail(), cwc.getCustomer().getPhone())));
    });
    return addressLines;
  }

  private List<String> contactLines(ApplicationJson application, CustomerRoleType roleType) {
    // returns {"[Yhteyshenkilön nimi]", "[Sähköpostiosoite, puhelin]"}

    Optional<CustomerWithContactsJson> cwcOpt =
        application.getCustomersWithContacts().stream().filter(cwc -> roleType.equals(cwc.getRoleType())).findFirst();
    final List<String> contactLines = new ArrayList<>();
    cwcOpt.ifPresent(cwc ->
      contactLines.addAll(
          cwc.getContacts().stream()
            .flatMap(c -> Stream.of(c.getName(), combinePossibleBlankStrings(c.getEmail(), c.getPhone())))
            .collect(Collectors.toList())));
    return contactLines;

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

  private String siteAddressLine(ApplicationJson application) {
    if (application.getLocations() == null || application.getLocations().isEmpty()) {
      return "";
    }
    final Map<Integer, FixedLocationJson> fixedLocationsById = fetchFixedLocations(application);
    return application.getLocations().stream().map(l -> locationAddress(l, fixedLocationsById))
        .collect(Collectors.joining(ADDRESS_LINE_SEPARATOR));
  }

  /*
   * If the application references any fixed locations, fetches all fixed
   * locations that match the application's kind and creates a lookup map.
   * Otherwise, just returns an empty map.
   */
  Map<Integer, FixedLocationJson> fetchFixedLocations(ApplicationJson applicationJson) {
    if (applicationJson.getLocations().stream().map(l -> l.getFixedLocationIds())
        .allMatch(flIds -> flIds == null || flIds.isEmpty())) {
      return Collections.emptyMap();
    } else {
      final ApplicationKind applicationKind = applicationJson.getKind();
      return locationService.getFixedLocationList().stream()
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
    if (locationJson.getFixedLocationIds() != null && !locationJson.getFixedLocationIds().isEmpty()) {
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
   * Return address like "Mannerheimintie 3, 00100 Helsinki", skip null/empty
   * values
   */
  public String postalAddress(PostalAddressJson a) {
    final String postalCodeAndCity = Arrays.asList(a.getPostalCode(), a.getCity()).stream()
        .filter(s -> s != null && !s.isEmpty())
        .collect(Collectors.joining(" "));
    return Arrays.asList(a.getStreetAddress(), postalCodeAndCity).stream()
        .filter(s -> s != null && !s.isEmpty())
        .collect(Collectors.joining(", "));
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
      return "";
    }
  }

  private int daysBetween(ZonedDateTime startDate, ZonedDateTime endDateExclusive) {
    if (startDate == null || endDateExclusive == null) {
      return 0;
    }
    return (int) (startDate.until(endDateExclusive, ChronoUnit.DAYS));
  }

  private UserJson findSupervisor(ApplicationJson application) {
    if (application.getLocations() == null) {
      return null;
    }

    final List<Integer> cityDistricts = application.getLocations().stream()
        .map(l -> l.getCityDistrictIdOverride() != null
            ? l.getCityDistrictIdOverride()
            : l.getCityDistrictId())
        .collect(Collectors.toList());
    if (!cityDistricts.isEmpty()) {
      final Integer cityDistrict = cityDistricts.get(0);
      final ApplicationType applicationType = application.getType();
      try {
        return locationService.findSupervisionTaskOwner(application.getType(), cityDistricts.get(0));
      } catch (NoSuchEntityException e) {
        logger.info("No owner for supervision tasks on city district {} for application type {}", cityDistrict, applicationType);
      }
    }
    return null;
  }

  private String translate(ApplicationKind kind) {
    return metaService.findTranslation("ApplicationKind", kind.name());
  }

  private String translate(ApplicationSpecifier specifier) {
    return metaService.findTranslation("ApplicationSpecifier", specifier.name());
  }


}
