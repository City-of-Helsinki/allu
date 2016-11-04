package fi.hel.allu.ui.service;

import com.google.common.base.Optional;

import fi.hel.allu.common.types.ApplicantType;
import fi.hel.allu.common.types.OutdoorEventNature;
import fi.hel.allu.model.domain.ApplicationPricing;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DecisionService {

  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(DecisionService.class);

  // Stylesheet name for decision PDF generation:
  private static final String DECISION_STYLESHEET = "paatos";

  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;
  private final ZoneId zoneId;
  private final Locale locale;
  private final DateTimeFormatter dateTimeFormatter;

  @Autowired
  public DecisionService(ApplicationProperties applicationProperties, RestTemplate restTemplate) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
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
        DECISION_STYLESHEET);
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

  private void fillJson(DecisionJson decisionJson, ApplicationJson application) {
    decisionJson.setEventName(application.getName());
    decisionJson.setDecisionId(application.getApplicationId());
    decisionJson.setApplicantAddressLines(applicantAddressLines(application));
    decisionJson.setApplicantContactLines(applicantContactLines(application));
    decisionJson.setSiteAddressLine(siteAddressLine(application));
    if (application.getLocation() != null) {
      decisionJson.setSiteArea(String.format("%.0f", Math.ceil(application.getLocation().getArea())));
    }
    OutdoorEventJson oe = (OutdoorEventJson) application.getEvent();
    if (oe != null) {
      decisionJson.setBuildStartDate(formatDateWithDelta(oe.getStructureStartTime(), 0));
      decisionJson.setBuildEndDate(formatDateWithDelta(oe.getEventStartTime(), -1));
      decisionJson.setTeardownStartDate(formatDateWithDelta(oe.getEventEndTime(), 1));
      decisionJson.setTeardownEndDate(formatDateWithDelta(oe.getStructureEndTime(), 0));

      decisionJson.setNumBuildAndTeardownDays(daysBetween(oe.getStructureStartTime(), oe.getEventStartTime())
          + daysBetween(oe.getEventEndTime(), oe.getStructureEndTime()));
      decisionJson.setReservationTimeExceptions(oe.getTimeExceptions());
      decisionJson.setEventDescription(oe.getDescription());
      decisionJson.setStructureArea(String.format("%.0f", oe.getStructureArea()));
      decisionJson.setStructureDescription(oe.getStructureDescription());
      decisionJson.setEventUrl(oe.getUrl());
      decisionJson.setHasCommercialActivities(oe.isSalesActivity());
      decisionJson.setSportsWithHeavyStructures(oe.isHeavyStructure());
      decisionJson.setHasEkokompassi(oe.isEcoCompass());
      decisionJson.setEventNature(eventNature(oe.getNature()));
      ApplicationPricing pricing = oe.getCalculatedPricing();
      if (pricing != null) {
        NumberFormat decimalFormat = NumberFormat.getCurrencyInstance(locale);
        decisionJson.setTotalRent(decimalFormat.format(pricing.getPrice() / 100.0));
        decisionJson.setSeparateBill(pricing.getPrice() > 0);
      }
      decisionJson.setPriceReason(oe.getNoPriceReason());
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
    decisionJson.setEventStartDate(formatDateWithDelta(application.getStartTime(), 0));
    decisionJson.setEventEndDate(formatDateWithDelta(application.getEndTime(), 0));
    decisionJson.setNumEventDays(daysBetween(application.getStartTime(), application.getEndTime()) + 1);
    decisionJson.setSiteAdditionalInfo("[Lisätietoja paikasta]");
    decisionJson.setDecisionDate("[päätöspvm]");
    decisionJson.setVatPercentage(99);
    decisionJson.setAdditionalConditions("[Ehtokentän teksti]");
    decisionJson.setDecisionTimestamp("[aikaleima]");
    decisionJson.setDeciderTitle("[päättäjän työnimike]");
    decisionJson.setDeciderName("[päättäjän nimi]");
    decisionJson.setAppealInstructions("[Muutoksenhakuohjeet]");
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
    if (applicant.getType() == ApplicantType.PERSON) {
      PersonJson p = applicant.getPerson();
      if (p == null) {
        return Collections.emptyList();
      }
      return Arrays.asList(String.format("%s, %s", p.getName(), p.getSsn()),
          postalAddress(p.getPostalAddress()),
          String.format("%s, %s", p.getEmail(), p.getPhone()));
    } else {
      OrganizationJson o = applicant.getOrganization();
      if (o == null) {
        return Collections.emptyList();
      }
      return Arrays.asList(String.format("%s, %s", o.getName(), o.getBusinessId()),
          postalAddress(o.getPostalAddress()),
          String.format("%s, %s", o.getEmail(), o.getPhone()));
    }
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
    LocationJson location = application.getLocation();
    if (location == null) {
      return "";
    }
    int ssId = Optional.fromNullable(application.getLocation().getFixedLocationId()).or(0);
    if (ssId != 0) {
      // TODO: cache all Fixed location IDs on startup and lookup from there
      return String.format("TODO: lohko %d", ssId);
    } else {
      return application.getLocation().getPostalAddress().getStreetAddress();
    }
  }

  private String postalAddress(PostalAddressJson a) {
    return String.format("%s, %s %s", a.getStreetAddress(), a.getPostalCode(), a.getCity());
  }

  private String eventNature(OutdoorEventNature nature) {
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
}
