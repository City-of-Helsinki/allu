package fi.hel.allu.common.domain.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Application tags that may be added to application.
 */
public enum ApplicationTagType {
  // täydennyspyyntö lähetetty
  ADDITIONAL_INFORMATION_REQUESTED(true),
  // lausunnolla
  STATEMENT_REQUESTED(true),
  // vakuus määritetty
  DEPOSIT_REQUESTED(false),
  // vakuus suoritettu
  DEPOSIT_PAID(false),
  // Aloitusvalvontapyyntö lähetetty
  PRELIMINARY_SUPERVISION_REQUESTED(false),
  // Aloitusvalvonta hylätty
  PRELIMINARY_SUPERVISION_REJECTED(false, PRELIMINARY_SUPERVISION_REQUESTED),
  // Aloitusvalvonta suoritettu
  PRELIMINARY_SUPERVISION_DONE(false, PRELIMINARY_SUPERVISION_REQUESTED, PRELIMINARY_SUPERVISION_REJECTED),
  // Valvontapyyntö lähetetty
  SUPERVISION_REQUESTED(false),
  // Valvonta hylätty
  SUPERVISION_REJECTED(false, SUPERVISION_REQUESTED),
  // Valvonta suoritettu
  SUPERVISION_DONE(false, SUPERVISION_REQUESTED, SUPERVISION_REJECTED),
  // odottaa. Hakemus odottaa lisätietoa, esimerkiksi selvitystä, mikä estää hakemuksen etenemisen
  WAITING(true),
  // hyvitysselvitys. käytetään esim. hyvityslaskujen selvittämisen aikana
  COMPENSATION_CLARIFICATION(true),
  // maksuperusteet korjattava
  PAYMENT_BASIS_CORRECTION(true),
  // toiminnallinen kunto ilmoitettu
  OPERATIONAL_CONDITION_REPORTED(false),
  // laskutettavan sap-tunnus ei tiedossa
  SAP_ID_MISSING(false),
  // Päätös lähettämättä
  DECISION_NOT_SENT(true),
  // Sopimusehdotus hylätty asiakasjärjestelmässä
  CONTRACT_REJECTED(true),
  // Aikamuutos
  DATE_CHANGE(false),
  // Kartoitettava
  SURVEY_REQUIRED(true),
  // Muut muutokset
  OTHER_CHANGES(false);

  private final List<ApplicationTagType> replaces;

  private final boolean manuallyAdded;

  private ApplicationTagType(boolean manuallyAdded) {
    replaces = new ArrayList<>();
    this.manuallyAdded = manuallyAdded;
  }

  private ApplicationTagType(boolean manuallyAdded, ApplicationTagType... replaces) {
    this.replaces = Arrays.asList(replaces);
    this.manuallyAdded = manuallyAdded;
  }

  public List<ApplicationTagType> getReplaces() {
    return replaces;
  }

  public boolean isManuallyAdded() {
    return manuallyAdded;
  }
}
