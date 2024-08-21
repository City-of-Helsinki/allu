package fi.hel.allu.servicecore.mapper;

import fi.hel.allu.common.domain.types.*;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.types.DefaultTextType;
import fi.hel.allu.common.types.EventNature;
import fi.hel.allu.common.util.CalendarUtil;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.domain.AbstractLocation;
import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.model.domain.util.EventDayUtil;
import fi.hel.allu.model.domain.util.PriceUtil;
import fi.hel.allu.model.domain.util.Printable;
import fi.hel.allu.pdf.domain.*;
import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.servicecore.service.*;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fi.hel.allu.common.util.TimeUtil.HelsinkiZoneId;
import static org.apache.commons.lang3.BooleanUtils.toBoolean;

@Component
public class DecisionJsonMapper extends AbstractDocumentMapper<DecisionJson> {

  private static final Logger logger = LoggerFactory.getLogger(DecisionJsonMapper.class);


  private static final Map<DefaultTextType, String> defaultTextTypeTranslations;
  private static final String UNDERPASS_YES = "Kyllä";
  private static final String UNDERPASS_NO = "Ei";
  private static final Set<ApplicationKind> vat0Kinds;

  static {
    Map<DefaultTextType, String> tempMap = new EnumMap<>(DefaultTextType.class);
    tempMap.put(DefaultTextType.TELECOMMUNICATION, "Tietoliikenne");
    tempMap.put(DefaultTextType.ELECTRICITY, "Sähkö");
    tempMap.put(DefaultTextType.WATER_AND_SEWAGE, "Vesi ja viemäri");
    tempMap.put(DefaultTextType.DISTRICT_HEATING_COOLING, "Kaukolämpö/jäähdytys");
    tempMap.put(DefaultTextType.GAS, "Kaasu");
    tempMap.put(DefaultTextType.UNDERGROUND_STRUCTURE, "Maanalainen rakenne/tila");
    tempMap.put(DefaultTextType.TRAMWAY, "Raitiotie");
    tempMap.put(DefaultTextType.STREET_HEATING, "Katulämmitys");
    tempMap.put(DefaultTextType.GEO_HEATING, "Maalämpö");
    tempMap.put(DefaultTextType.SEWAGE_PIPE, "Jäteputki");
    tempMap.put(DefaultTextType.GEOTHERMAL_WELL, "Maalämpökaivo");
    tempMap.put(DefaultTextType.GEOTECHNICAL_OBSERVATION_POST, "Geotekninen tarkkailupiste");
    tempMap.put(DefaultTextType.OTHER, "Yleisesti/muut");
    defaultTextTypeTranslations = Collections.unmodifiableMap(tempMap);
    vat0Kinds = new HashSet<>(Arrays.asList(
            ApplicationKind.SUMMER_TERRACE,
            ApplicationKind.WINTER_TERRACE,
            ApplicationKind.PARKLET,
            ApplicationKind.URBAN_FARMING));
  }

  private final NumberFormat currencyFormat;
  private final NumberFormat percentageFormat;

  private final MetaService metaService;
  private final ChargeBasisService chargeBasisService;
  private final DecimalFormat decimalFormat;

  @Autowired
  public DecisionJsonMapper(LocationService locationService,
      CustomerService customerService,
      ContactService contactService,
      ChargeBasisService chargeBasisService,
      MetaService metaService) {
    super(customerService, contactService, locationService);
    this.chargeBasisService = chargeBasisService;
    this.metaService = metaService;
    decimalFormat = new DecimalFormat("0.##");
    Locale locale = new Locale("fi", "FI");
    currencyFormat = NumberFormat.getCurrencyInstance(locale);
    percentageFormat = NumberFormat.getNumberInstance(locale);
  }


  public DecisionJson mapToDocumentJson(ApplicationJson application, boolean draft) {
    DecisionJson decisionJson = new DecisionJson();
    decisionJson.setDraft(draft);
    decisionJson.setAnonymizedDocument(getCustomerAnonymizer().isPresent());
    decisionJson.setEventName(application.getName());
    decisionJson.setDecisionId(application.getApplicationId());
    decisionJson.setCustomerAddressLines(customerAddressLines(application));
    decisionJson.setCustomerContactLines(customerContactLines(application));
    decisionJson.setApplicantName(applicantName(application));
    decisionJson.setSiteAddressLine(siteAddressLine(application));
    decisionJson.setSiteCityDistrict(siteCityDistrict(application));

    final Map<ApplicationKind, List<ApplicationSpecifier>> applicationsKindsWithSpecifiers = application.getKindsWithSpecifiers();
    final List<KindWithSpecifiers> kindsWithSpecifiers = new ArrayList<>();
    applicationsKindsWithSpecifiers.keySet().forEach(kind -> {
      final List<String> specifiers = new ArrayList<>();
      applicationsKindsWithSpecifiers.get(kind).forEach(s -> specifiers.add(translate(s)));
      KindWithSpecifiers k = new KindWithSpecifiers();
      k.setKind(translate(kind));
      k.setSpecifiers(specifiers);
      kindsWithSpecifiers.add(k);
    });
    decisionJson.setKinds(kindsWithSpecifiers);
    decisionJson.setAppealInstructions("[Muutoksenhakuohjeet]");
    decisionJson.setNotBillable(Boolean.TRUE.equals(application.getNotBillable()));
    decisionJson.setNotBillableReason(application.getNotBillableReason());
    fillCargeBasisInfo(decisionJson, application);
    decisionJson.setIdentificationNumber(application.getIdentificationNumber());
    decisionJson.setReplacingDecision(application.getReplacesApplicationId() != null);

    getSiteArea(application.getLocations()).ifPresent(decisionJson::setSiteArea);
    decisionJson.setCustomerReference(application.getCustomerReference());
    decisionJson.setInvoicingPeriodLength(application.getInvoicingPeriodLength());
    decisionJson.setVatPercentage("25,5"); // FIXME: find actual value somehow

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
      break;
    case EXCAVATION_ANNOUNCEMENT:
      fillExcavationAnnouncementSpecifics(decisionJson, application);
      break;
    case AREA_RENTAL:
      fillAreaRentalSpecifics(decisionJson, application);
      break;
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
      decisionJson.setAttachmentNames(attachments.stream().filter(AttachmentInfoJson::isDecisionAttachment)
          .map(a -> StringUtils.defaultIfEmpty(a.getDescription(), a.getName()))
          .collect(Collectors.toList()));
    }
    decisionJson.setReservationStartDate(application.getStartTime());
    decisionJson.setReservationEndDate(application.getEndTime());
    decisionJson.setNumReservationDays(daysBetween(application.getStartTime(), application.getEndTime()) + 1);
    decisionJson.setRecurringEndTime(application.getRecurringEndTime());

    String additionalInfos = streamFor(application.getLocations()).map(LocationJson::getAdditionalInfo).filter(Objects::nonNull)
        .map(String::trim).filter(p -> !p.isEmpty()).collect(Collectors.joining("; "));
    decisionJson.setSiteAdditionalInfo(additionalInfos);
    decisionJson.setDecisionDate(
        Optional.ofNullable(application.getDecisionTime()).map(dt -> formatDateWithDelta(dt, 0)).orElse("[Päätöspvm]"));
    decisionJson.setAdditionalConditions(
        splitToList(Optional.ofNullable(application.getExtension()).map(ApplicationExtensionJson::getTerms)));
    decisionJson.setDecisionTimestamp(TimeUtil.dateAsDateTimeString(ZonedDateTime.now()));
    UserJson decider = application.getDecisionMaker();
    if (decider != null) {
      decisionJson.setDeciderTitle(decider.getTitle());
      decisionJson.setDeciderName(decider.getRealName());
    }
    convertNonBreakingSpacesToSpaces(decisionJson);
    return decisionJson;
  }

  private Optional<String> getSiteArea(List<LocationJson> locations) {
    return Optional.ofNullable(locations)
        .map(locs -> locs.stream().mapToDouble(this::getEffectiveArea).sum())
        .map(Math::ceil)
        .map(totalArea -> String.format("%.0f", totalArea));
  }

  private Double getEffectiveArea(LocationJson location) {
    return Optional.ofNullable(location.getAreaOverride())
      .orElse(location.getArea());
  }

  /*
   * Read application's charge basis entries, order them, and generate matching
   * charge info texts.
   */
  private void fillCargeBasisInfo(DecisionJson decisionJson, ApplicationJson application) {
    if (application.getId() == null) {
      return;
    }
    List<ChargeBasisEntry> chargeBasisEntries = chargeBasisService.getInvoicableChargeBasis(application.getId());
    fillCargeBasisInfo(decisionJson, chargeBasisEntries);
  }

  protected void fillCargeBasisInfo(DecisionJson decisionJson, List<ChargeBasisEntry> chargeBasisEntries) {
    Map<String, List<ChargeBasisEntry>> entriesByReferred = chargeBasisEntries.stream()
        .collect(Collectors.groupingBy(cbe -> StringUtils.defaultString(cbe.getReferredTag())));
    List<Pair<Integer, ChargeBasisEntry>> orderedEntries = listReferringEntries(entriesByReferred, "", 0);
    decisionJson.setChargeInfoEntries(orderedEntries.stream().map(p -> new ChargeInfoTexts(p.getLeft(),
        p.getRight().getText(), p.getRight().getExplanation(),
        chargeQuantity(p.getRight()), chargeUnitPrice(p.getRight()), chargeNetPrice(p.getRight())))
        .collect(Collectors.toList()));

    if (!orderedEntries.isEmpty()) {
      final int priceInCents = PriceUtil.totalPrice(chargeBasisEntries);
      decisionJson.setTotalRent(currencyFormat.format(priceInCents / 100.0));
      decisionJson.setSeparateBill(priceInCents > 0);
    }
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
      case METER:
        return " m";
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
      return percentageFormat.format(e.getQuantity()) + " %";
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
    }
    if (ApplicationKind.BRIDGE_BANNER.equals(application.getKind())) {
      // For bridge banners, site area should be skipped in printout
      decisionJson.setSiteArea(null);
    }
    if (vat0Kinds.contains(application.getKind())) {
      decisionJson.setVatPercentage("0");
    }

    decisionJson.setRepresentativeAddressLines(addressLines(application, CustomerRoleType.REPRESENTATIVE));
    decisionJson.setRepresentativeContactLines(contactLines(application, CustomerRoleType.REPRESENTATIVE));

    if (application.getDecisionDistributionList() != null && !application.getDecisionDistributionList().isEmpty()) {
      decisionJson.setDistributionNames(application.getDecisionDistributionList().stream()
        .map(DistributionEntryJson::getName).collect(Collectors.toList()));
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
      decisionJson.setRepresentativeAddressLines(addressLines(application, CustomerRoleType.REPRESENTATIVE));
      decisionJson.setRepresentativeContactLines(contactLines(application, CustomerRoleType.REPRESENTATIVE));
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
          .map(i -> new CableInfoTexts(defaultTextTypeTranslations.get(i.getType()), Arrays.stream(i.getAdditionalInfo().split("\n")).collect(
                  Collectors.toList())))
          .collect(Collectors.toList()));
      decisionJson.setMapExtractCount(Optional.ofNullable(cableReportJson.getMapExtractCount()).orElse(0));
    }
    // Override customer contact & address lines
    Optional<Pair<CustomerJson, ContactJson>> orderer = cableReportOrderer(applicationJson);

    decisionJson.setCustomerContactLines(cableReportContactLines(orderer));
    decisionJson.setCableReportOrderer(
        orderer.map(e -> convertNonBreakingForwardSlashToBreaking(e.getValue().getName())).orElse("[Johtoselvityksen tilaajan nimi puuttuu]"));
    decisionJson.setCustomerAddressLines(cableReportAddressLines(applicationJson));
  }

  private void fillPlacementContractSpecifics(DecisionJson decisionJson, ApplicationJson applicationJson) {
    PlacementContractJson placementContract = (PlacementContractJson)applicationJson.getExtension();
    if (placementContract != null) {
      decisionJson.setSectionNumber(placementContract.getSectionNumber());
      decisionJson.setContractText(splitToList(Optional.ofNullable(placementContract.getContractText())));
      decisionJson.setRationale(splitToList(Optional.ofNullable(placementContract.getRationale())));
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
    decision.setTrafficArrangements(splitToList(Optional.ofNullable(trafficArrangement.getTrafficArrangements())));
  }

  private void fillExcavationAnnouncementSpecifics(DecisionJson decision, ApplicationJson application) {
    ExcavationAnnouncementJson excavationAnnouncement = (ExcavationAnnouncementJson)application.getExtension();
    setContacts(decision, application);
    decision.setWorkPurpose(excavationAnnouncement.getWorkPurpose());
    decision.setWinterTimeOperation(formatDateWithDelta(excavationAnnouncement.getWinterTimeOperation(), 0));
    decision.setCustomerWinterTimeOperation(formatDateWithDelta(excavationAnnouncement.getCustomerWinterTimeOperation(), 0));
    decision.setWorkFinished(formatDateWithDelta(excavationAnnouncement.getWorkFinished(), 0));
    decision.setCustomerWorkFinished(formatDateWithDelta(excavationAnnouncement.getCustomerWorkFinished(), 0));
    decision.setTrafficArrangements(splitToList(Optional.ofNullable(excavationAnnouncement.getTrafficArrangements())));
    decision.setCompactionAndBearingCapacityMeasurement(excavationAnnouncement.getCompactionAndBearingCapacityMeasurement());
    decision.setQualityAssuranceTest(excavationAnnouncement.getQualityAssuranceTest());
    decision.setGuaranteeEndTime(formatDateWithDelta(excavationAnnouncement.getGuaranteeEndTime(), 0));
    decision.setPlacementContracts(listToString(excavationAnnouncement.getPlacementContracts()));
    decision.setCableReports(listToString(excavationAnnouncement.getCableReports()));
    decision.setHeaderRows(countHeaderRows(decision, Arrays.asList(
        DecisionJson::isReplacingDecision,
        DecisionJson::getIdentificationNumber)));
  }

  private void fillAreaRentalSpecifics(DecisionJson decision, ApplicationJson application) {
    AreaRentalJson areaRental = (AreaRentalJson)application.getExtension();
    setContacts(decision, application);
    decision.setWorkPurpose(areaRental.getWorkPurpose());
    decision.setWorkFinished(formatDateWithDelta(areaRental.getWorkFinished(), 0));
    decision.setCustomerWorkFinished(formatDateWithDelta(areaRental.getCustomerWorkFinished(), 0));
    decision.setTrafficArrangements(splitToList(Optional.ofNullable(areaRental.getTrafficArrangements())));
    Set<String> addresses = new HashSet<>();
    application.getLocations().forEach(l -> addresses.add(l.getAddress()));
    decision.setAreaAddresses(new ArrayList<>(addresses));

    final Map<Integer, Location> locations = locationService.getLocationsByApplication(application.getId())
        .stream().collect(Collectors.toMap(AbstractLocation::getId, l -> l));
    final List<ChargeBasisEntry> chargeBasisEntries = chargeBasisService.getSingleInvoiceChargeBasis(application.getId());
    final List<ChargeBasisEntry> areaEntries = getAreaEntries(chargeBasisEntries, application.getId());
    final List<ChargeBasisEntry> otherEntries = BooleanUtils.isTrue(application.getNotBillable()) ? Collections.emptyList()
        : chargeBasisEntries.stream().filter(c -> !isAreaEntry(c, chargeBasisEntries) && c.isInvoicable())
            .collect(Collectors.toList());
    decision.setHasAreaEntries(!areaEntries.isEmpty());
    final List<RentalArea> rentalAreas = areaEntries.stream()
        .map(e -> chargeBasisToRentalArea(e, application, locations, areaEntries))
        .collect(Collectors.toList());
    final List<RentalArea> otherRentalAreas = otherEntries.stream()
        .map(e -> chargeBasisToRentalArea(e, application, locations, otherEntries))
        .collect(Collectors.toList());
    if (!otherRentalAreas.isEmpty()) {
      otherRentalAreas.get(0).setFirstCommon(true);
      rentalAreas.addAll(otherRentalAreas);
    }
    decision.setRentalAreas(rentalAreas);
  }


  // Gets area entries and entries referring to area entries. There can be several entries for one location if entries
  // are splitted to invoicing periods -> filter duplicate entries
  private List<ChargeBasisEntry> getAreaEntries(List<ChargeBasisEntry> allEntries, int applicationId) {
    List<ChargeBasisEntry> result = new ArrayList<>();
    List<ChargeBasisEntry> areaEntries = allEntries.stream().filter(e -> e.getLocationId() != null).collect(Collectors.toList());
    areaEntries.forEach(ae -> ae.setNetPrice(chargeBasisService.getInvoicableSumForLocation(applicationId, ae.getLocationId())));
    areaEntries.forEach(ae -> addWithReferringEntries(ae, allEntries, result));
    return result;
  }

  private void addWithReferringEntries(ChargeBasisEntry areaEntry, List<ChargeBasisEntry> allEntries,
      List<ChargeBasisEntry> result) {
    result.add(areaEntry);
    result.addAll(allEntries.stream().filter(e -> Objects.equals(e.getReferredTag(), areaEntry.getTag()))
        .collect(Collectors.toList()));
  }

  private boolean isAreaEntry(ChargeBasisEntry entry, List<ChargeBasisEntry> allEntries) {
    return entry.getLocationId() != null ||
        (entry.getReferredTag() != null && getReferredEntry(entry.getReferredTag(), allEntries)
            .map(ChargeBasisEntry::getLocationId).orElse(null) != null);
  }

  private Optional<ChargeBasisEntry> getReferredEntry(String tag, List<ChargeBasisEntry> allEntries) {
    return allEntries.stream().filter(e -> e.getTag() != null && e.getTag().equals(tag)).findFirst();
  }

  private RentalArea chargeBasisToRentalArea(ChargeBasisEntry entry, ApplicationJson application,
      Map<Integer, Location> locations, List<ChargeBasisEntry> entries) {

    final RentalArea rentalArea = new RentalArea();
    if (BooleanUtils.isNotTrue(application.getNotBillable()) && ( entry.getNetPrice() != 0 || ChargeBasisUnit.PERCENT.equals(entry.getUnit()) )) {
      rentalArea.setUnitPrice(chargeUnitPrice(entry));
      rentalArea.setPrice(chargeNetPrice(entry));
    }
    rentalArea.setQuantity(chargeQuantity(entry));

    if (entry.getLocationId() != null) {
      final Location location = locations.get(entry.getLocationId());
      rentalArea.setAreaId(application.getApplicationId() + "/" + location.getLocationKey());

      final ZonedDateTime startTime = location.getStartTime().withZoneSameInstant(HelsinkiZoneId);
      final ZonedDateTime endTime = location.getEndTime().withZoneSameInstant(HelsinkiZoneId);

      rentalArea.setFinished(ZonedDateTime.now().isAfter(endTime));

      final String period = Printable.forDayPeriod(startTime, endTime);
      rentalArea.setTime(period);
      if (location.getPostalAddress() != null) {
        rentalArea.setAddress(location.getPostalAddress().getStreetAddress());
      }
      rentalArea.setUnderpass(toBoolean(location.getUnderpass()) ? UNDERPASS_YES : UNDERPASS_NO);
      rentalArea.setArea(((int)Math.ceil(location.getEffectiveArea())) + " m²");
      rentalArea.setPaymentClass(PriceUtil.getPaymentClassText(location.getEffectivePaymentTariff()));
      rentalArea.setDays(Integer.toString((int)CalendarUtil.startingUnitsBetween(startTime, endTime, ChronoUnit.DAYS)));
      rentalArea.setAdditionalInfo(location.getAdditionalInfo());

    } else if (entry.getReferredTag() != null) {
      rentalArea.setText(entry.getText());
      getReferredEntry(entry.getReferredTag(), entries)
          .ifPresent(e -> rentalArea.setFinished(e.getLocationId() != null && ZonedDateTime.now().isAfter(locations.get(e.getLocationId()).getEndTime())));
    } else {
      rentalArea.setChargeBasisText(entry.getText());
    }
    return rentalArea;
  }

  private void setContacts(DecisionJson decision, ApplicationJson application) {
    decision.setContractorAddressLines(addressLines(application, CustomerRoleType.CONTRACTOR));
    decision.setContractorContactLines(contactLines(application, CustomerRoleType.CONTRACTOR));
    decision.setPropertyDeveloperAddressLines(addressLines(application, CustomerRoleType.PROPERTY_DEVELOPER));
    decision.setPropertyDeveloperContactLines(contactLines(application, CustomerRoleType.PROPERTY_DEVELOPER));
    decision.setRepresentativeAddressLines(addressLines(application, CustomerRoleType.REPRESENTATIVE));
    decision.setRepresentativeContactLines(contactLines(application, CustomerRoleType.REPRESENTATIVE));
    if (application.getInvoiceRecipientId() != null) {
      CustomerJson customer = findCustomerById(application.getInvoiceRecipientId());
      decision.setInvoiceRecipientAddressLines(addressLines(Optional.of(customer)));
      decision.setOvt(customer.getOvt());
      decision.setInvoicingOperator(customer.getInvoicingOperator());
    }
  }

  /* Find the customer and contact that ordered the application */
  private Optional<Pair<CustomerJson, ContactJson>> cableReportOrderer(
      ApplicationJson applicationJson) {
    CableReportJson cableReport = (CableReportJson)applicationJson.getExtension();
    return Optional.ofNullable(cableReport.getOrderer())
        .map(this::findContactById)
        .map(contact -> Pair.of(findCustomerById(contact.getCustomerId()), contact));
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
    return Stream.of(
        convertNonBreakingForwardSlashToBreaking(customer.getName()),
        convertNonBreakingForwardSlashToBreaking(contact.getName()), contact.getPhone(), contact.getEmail())
            .filter(p -> p != null && !p.trim().isEmpty()).collect(Collectors.toList());
  }

  /*
   * For cable reports, the customer address data should come from the customer
   * that is doing the work
   */
  private List<String> cableReportAddressLines(ApplicationJson applicationJson) {
    CustomerWithContactsJson contractor = getCustomerByRole(applicationJson, CustomerRoleType.CONTRACTOR).orElse(null);
    if (contractor == null) {
      return Collections.singletonList("[Kaivajan tiedot puuttuvat]");
    }
    final CustomerJson customer = contractor.getCustomer();
    return Stream.of(convertNonBreakingForwardSlashToBreaking(customer.getName()))
            .filter(p -> p != null && !p.trim().isEmpty()).collect(Collectors.toList());

  }
  private String formatDateWithDelta(ZonedDateTime zonedDateTime, int deltaDays) {
    if (zonedDateTime == null) {
      return null;
    }
    return TimeUtil.dateAsString(zonedDateTime.plusDays(deltaDays));
  }

  private String applicantName(ApplicationJson applicationJson) {
    Optional<CustomerWithContactsJson> cwcOpt = getCustomerByRole(applicationJson, CustomerRoleType.APPLICANT);
    return cwcOpt.map(cwc -> cwc.getCustomer().getName()).orElse(null);
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

  private String listToString(List<String> list) {
    if (list != null && !list.isEmpty()) {
      return String.join(", ",list);
    }
    return null;
  }

  private int countHeaderRows(DecisionJson decision, List<Function<DecisionJson, Object>> getters) {
    return (int)getters.stream().map(f -> f.apply(decision)).filter(Objects::nonNull).count();
  }

  private String translate(ApplicationKind kind) {
    return metaService.findTranslation("ApplicationKind", kind.name());
  }

  private String translate(ApplicationSpecifier specifier) {
    return metaService.findTranslation("ApplicationSpecifier", specifier.name());
  }
}
