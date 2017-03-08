package fi.hel.allu.ui.service;

import fi.hel.allu.common.types.ApplicationKind;
import fi.hel.allu.common.types.EventNature;
import fi.hel.allu.pdf.domain.DecisionJson;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.*;

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

  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;
  private LocationService locationService;
  private final ZoneId zoneId;
  private final Locale locale;
  private final DateTimeFormatter dateTimeFormatter;

  static {
    BAD_LOCATION = new FixedLocationJson();
    BAD_LOCATION.setArea("Tuntematon alue");
  }

  @Autowired
  public DecisionService(ApplicationProperties applicationProperties, RestTemplate restTemplate,
      LocationService locationService) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.locationService = locationService;
    zoneId = ZoneId.of("Europe/Helsinki");
    locale = new Locale("fi", "FI");
    dateTimeFormatter = DateTimeFormatter.ofPattern("d.M.uuuu");
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
   * @param applicationId
   *          the application's ID
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
    decisionJson.setApplicantAddressLines(applicantAddressLines(application));
    decisionJson.setApplicantContactLines(applicantContactLines(application));
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
    decisionJson.setSiteAdditionalInfo("[Lisätietoja paikasta]");
    decisionJson.setDecisionDate("[päätöspvm]");
    decisionJson.setVatPercentage(99);
    decisionJson.setAdditionalConditions("[Ehtokentän teksti]");
    decisionJson.setDecisionTimestamp("[aikaleima]");
    decisionJson.setDeciderTitle("[päättäjän työnimike]");
    decisionJson.setDeciderName("[päättäjän nimi]");
    decisionJson.setAppealInstructions("[Muutoksenhakuohjeet]");
    Integer priceInCents = (application.getPriceOverride() != null) ? application.getPriceOverride()
        : application.getCalculatedPrice();
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
      decisionJson.setHasCommercialActivities(ej.isSalesActivity());
      decisionJson.setSportsWithHeavyStructures(ej.isHeavyStructure());
      decisionJson.setHasEkokompassi(ej.isEcoCompass());
      decisionJson.setEventNature(eventNature(ej.getNature()));
      decisionJson.setPriceReason(ej.getNoPriceReason());
    }
  }

  private String formatDateWithDelta(ZonedDateTime zonedDateTime, int deltaDays) {
    if (zonedDateTime == null) {
      return null;
    }
    return zonedDateTime.plusDays(deltaDays).withZoneSameInstant(zoneId)
        .format(dateTimeFormatter);
  }

  private List<String> applicantAddressLines(ApplicationJson applicationJson) {
    // return lines in format {"[Applicant name], [SSID]", "[address, Postal
    // code + city]",
    // "[email, phone]"}
    ApplicantJson applicant = applicationJson.getApplicant();
    if (applicant == null) {
      return Collections.emptyList();
    }
    return Arrays.asList(
        String.format("%s, %s", applicant.getName(), applicant.getRegistryKey()),
        postalAddress(applicant.getPostalAddress()),
        String.format("%s, %s", applicant.getEmail(), applicant.getPhone()));
  }

  private List<String> applicantContactLines(ApplicationJson application) {
    // returns {"[Yhteyshenkilön nimi]", "[Sähköpostiosoite, puhelin]"}
    if (application.getContactList() == null) {
      return Collections.emptyList();
    }
    return application.getContactList().stream()
        .flatMap(c -> Stream.of(c.getName(), String.format("%s, %s", c.getEmail(), c.getPhone())))
        .collect(Collectors.toList());
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

  private String postalAddress(PostalAddressJson a) {
    return String.format("%s, %s %s", a.getStreetAddress(), a.getPostalCode(), a.getCity());
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
    return application.getType().name();
  }
}
