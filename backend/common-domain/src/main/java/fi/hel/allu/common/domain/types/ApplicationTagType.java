package fi.hel.allu.common.domain.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Application tags that may be added to application.
 */
public enum ApplicationTagType {
  // täydennyspyyntö lähetetty
  ADDITIONAL_INFORMATION_REQUESTED,
  // lausunnolla
  STATEMENT_REQUESTED,
  // vakuus määritetty
  DEPOSIT_REQUESTED,
  // vakuus suoritettu
  DEPOSIT_PAID,
  // Aloitusvalvontapyyntö lähetetty
  PRELIMINARY_SUPERVISION_REQUESTED,
  // Aloitusvalvonta hylätty
  PRELIMINARY_SUPERVISION_REJECTED(PRELIMINARY_SUPERVISION_REQUESTED),
  // Aloitusvalvonta suoritettu
  PRELIMINARY_SUPERVISION_DONE(PRELIMINARY_SUPERVISION_REQUESTED, PRELIMINARY_SUPERVISION_REJECTED),
  // Valvontapyyntö lähetetty
  SUPERVISION_REQUESTED,
  // Valvonta hylätty
  SUPERVISION_REJECTED(SUPERVISION_REQUESTED),
  // Valvonta suoritettu
  SUPERVISION_DONE(SUPERVISION_REQUESTED, SUPERVISION_REJECTED),
  // odottaa. Hakemus odottaa lisätietoa, esimerkiksi selvitystä, mikä estää hakemuksen etenemisen
  WAITING,
  // hyvitysselvitys. käytetään esim. hyvityslaskujen selvittämisen aikana
  COMPENSATION_CLARIFICATION,
  // maksuperusteet korjattava
  PAYMENT_BASIS_CORRECTION,
  // toiminnallinen kunto ilmoitettu
  OPERATIONAL_CONDITION_REPORTED,
  // laskutettavan sap-tunnus ei tiedossa
  SAP_ID_MISSING,
  // Päätös lähettämättä
  DECISION_NOT_SENT,
  // Sopimusehdotus hylätty asiakasjärjestelmässä
  CONTRACT_REJECTED,
  // Aikamuutos
  DATE_CHANGE,
  // Kartoitettava
  SURVEY_REQUIRED,
  // Muut muutokset
  OTHER_CHANGES;

  private final List<ApplicationTagType> replaces;

  private ApplicationTagType() {
    replaces = new ArrayList<>();
  }

  private ApplicationTagType(ApplicationTagType... replaces) {
    this.replaces = Arrays.asList(replaces);
  }

  public List<ApplicationTagType> getReplaces() {
    return replaces;
  }
}
