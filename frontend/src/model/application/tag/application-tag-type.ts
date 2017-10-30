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
  // työn valmistuminen ilmoitettu
  WORK_READY_REPORTED,
  // työn valmistuminen hylätty
  WORK_READY_REJECTED,
  // työn valmistuminen hyväksytty
  WORK_READY_ACCEPTED,
  // laskutettavan sap-tunnus ei tiedossa
  SAP_ID_MISSING
}

export const manualTagTypes = [
  ApplicationTagType.WAITING,
  ApplicationTagType.ADDITIONAL_INFORMATION_REQUESTED,
  ApplicationTagType.STATEMENT_REQUESTED,
  ApplicationTagType.DEPOSIT_PAID,
  ApplicationTagType.COMPENSATION_CLARIFICATION,
  ApplicationTagType.PAYMENT_BASIS_CORRECTION
];
