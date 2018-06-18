export enum ApplicationTagType {
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
  PRELIMINARY_SUPERVISION_REJECTED,
  // Aloitusvalvonta suoritettu
  PRELIMINARY_SUPERVISION_DONE,
  // Valvontapyyntö lähetetty
  SUPERVISION_REQUESTED,
  // Valvonta hylätty
  SUPERVISION_REJECTED,
  // Valvonta suoritettu
  SUPERVISION_DONE,
  // odottaa. Hakemus odottaa lisätietoa, esimerkiksi selvitystä, mikä estää hakemuksen etenemisen
  WAITING,
  // hyvitysselvitys. käytetään esim. hyvityslaskujen selvittämisen aikana
  COMPENSATION_CLARIFICATION,
  // maksuperusteet korjattava
  PAYMENT_BASIS_CORRECTION,
  // toiminnallinen kunto ilmoitettu
  OPERATIONAL_CONDITION_REPORTED,
  // toiminnallinen kunto hylätty
  OPERATIONAL_CONDITION_REJECTED,
  // toiminnallinen kunto hyväksytty
  OPERATIONAL_CONDITION_ACCEPTED,
  // Loppuvalvontapyyntö lähetetty
  FINAL_SUPERVISION_REQUESTED,
  // Loppuvalvonta hylätty
  FINAL_SUPERVISION_REJECTED,
  // Loppuvalvonta hyväksytty
  FINAL_SUPERVISION_ACCEPTED,
  // laskutettavan sap-tunnus ei tiedossa
  SAP_ID_MISSING,
  // Päätös lähettämättä
  DECISION_NOT_SENT,
  // Sopimusta ei hyväksytty
  CONTRACT_REJECTED
}

export const manualTagTypes = [
  ApplicationTagType.WAITING,
  ApplicationTagType.ADDITIONAL_INFORMATION_REQUESTED,
  ApplicationTagType.STATEMENT_REQUESTED,
  ApplicationTagType.COMPENSATION_CLARIFICATION,
  ApplicationTagType.PAYMENT_BASIS_CORRECTION,
  ApplicationTagType.DECISION_NOT_SENT,
  ApplicationTagType.CONTRACT_REJECTED
];
