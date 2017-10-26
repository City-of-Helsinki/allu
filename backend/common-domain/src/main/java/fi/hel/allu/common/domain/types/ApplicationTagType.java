package fi.hel.allu.common.domain.types;

/**
 * Application tags that may be added to application.
 */
public enum ApplicationTagType {
  ADDITIONAL_INFORMATION_REQUESTED,   // täydennyspyyntö lähetetty
  STATEMENT_REQUESTED,                // lausunnolla
  DEPOSIT_REQUESTED,                  // vakuus määritetty
  DEPOSIT_PAID,                       // vakuus suoritettu
  PRELIMINARY_SUPERVISION_REQUESTED,  // Aloitusvalvontapyyntö lähetetty
  PRELIMINARY_SUPERVISION_DONE,       // Aloitusvalvonta suoritettu
  SUPERVISION_REQUESTED,              // Valvontapyyntö lähetetty
  SUPERVISION_DONE,                   // Valvonta suoritettu
  WAITING,                            // odottaa. Hakemus odottaa lisätietoa, esimerkiksi selvitystä, mikä estää hakemuksen etenemisen
  COMPENSATION_CLARIFICATION,         // hyvitysselvitys. käytetään esim. hyvityslaskujen selvittämisen aikana
  PAYMENT_BASIS_CORRECTION,           // maksuperusteet korjattava
  OPERATIONAL_CONDITION_REPORTED,     // toiminnallinen kunto ilmoitettu
  OPERATIONAL_CONDITION_ACCEPTED,     // toiminnallinen kunto hyväksytty
  OPERATIONAL_CONDITION_REJECTED,     // toiminnallinen kunto hylätty
  WORK_READY_REPORTED,                // työn valmistuminen ilmoitettu
  WORK_READY_ACCEPTED,                // työn valmistuminen hyväksytty
  WORK_READY_REJECTED,                // työn valmistuminen hylätty
  SAP_ID_MISSING                      // laskutettavan sap-tunnus ei tiedossa
}
