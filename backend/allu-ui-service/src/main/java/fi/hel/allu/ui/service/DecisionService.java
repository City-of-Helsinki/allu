package fi.hel.allu.ui.service;

import fi.hel.allu.pdf.domain.DecisionJson;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.ApplicationJson;

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
import java.util.Arrays;

@Service
public class DecisionService {

  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(DecisionService.class);

  // Stylesheet name for decision PDF generation:
  private static final String DECISION_STYLESHEET = "paatos";

  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;

  @Autowired
  public DecisionService(ApplicationProperties applicationProperties, RestTemplate restTemplate) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
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

  private void fillJson(DecisionJson decisionJson, ApplicationJson application) {
    decisionJson.setDecisionDate("[päätöspvm]");
    decisionJson.setDecisionId("[tunnus]");
    decisionJson.setApplicantAddressLines(Arrays.asList("[Hakijan nimi], [Y-tunnus]",
        "[Osoite, postinumero, toimipaikka]", "[Sähköpostiosoite, puhelin]"));
    decisionJson.setApplicantContactLines(Arrays.asList("[Yhteyshenkilön nimi]", "[Sähköpostiosoite, puhelin]"));
    decisionJson.setSiteAddressLine("[Vuokrattava paikka, Lohko(t)], [Osoite]");
    decisionJson.setSiteAdditionalInfo("Lisätietoja paikasta]");
    decisionJson.setSiteArea("[Alueen pinta-ala]");
    decisionJson.setBuildStartDate("[Rakentamisen alkupäivämäärä]");
    decisionJson.setBuildEndDate("[Tapahtuman alkupäivämäärä-1]");
    decisionJson.setEventStartDate("[Tapahtuman alkupäivämäärä]");
    decisionJson.setEventEndDate("[Tapahtuman loppupäivämäärä]");
    decisionJson.setTeardownStartDate("[Tapahtuman loppupäivämäärä+1]");
    decisionJson.setTeardownEndDate("[Purkamisen loppupäivämäärä]");
    decisionJson.setNumEventDays(99999);
    decisionJson.setNumBuildAndTeardownDays(88888);
    decisionJson.setReservationTimeExceptions("[Tapahtuma-ajan poikkeukset]");
    decisionJson.setEventName("[Tapahtuman nimi]");
    decisionJson.setEventDescription("[Tapahtuman kuvaus]");
    decisionJson.setEventUrl("http://example.to/event");
    decisionJson.setEventNature("[Tapahtuman luonne]");
    decisionJson.setStructureArea("[rakenteiden kokonaisneliömäärä]");
    decisionJson.setStructureDescription("[Rakenteiden kuvaus]");
    decisionJson.setTotalRent("[Hinta]");
    decisionJson.setVatPercentage(99);
    decisionJson.setPriceReason("XXX");
    decisionJson.setHasCommercialActivities(true);
    decisionJson.setSportsWithHeavyStructures(true);
    decisionJson.setHasEkokompassi(true);
    decisionJson.setSeparateBill(true);
    decisionJson.setAdditionalConditions("[Ehtokentän teksti]");
    decisionJson.setDecisionTimestamp("[aikaleima]");
    decisionJson.setDeciderTitle("[päättäjän työnimike]");
    decisionJson.setDeciderName("[päättäjän nimi]");
    decisionJson.setHandlerTitle("[titteli]");
    decisionJson.setHandlerName("[tarkastajan nimi]");
    decisionJson.setAttachmentNames(Arrays.asList("[Lista liitteiden nimistä]"));
    decisionJson.setAppealInstructions("[Muutoksenhakuohjeet]");
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

}
