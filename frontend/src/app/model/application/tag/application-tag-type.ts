export enum ApplicationTagType {
  // täydennyspyyntö lähetetty
  ADDITIONAL_INFORMATION_REQUESTED = 'ADDITIONAL_INFORMATION_REQUESTED',
  // lausunnolla
  STATEMENT_REQUESTED = 'STATEMENT_REQUESTED',
  // vakuus määritetty
  DEPOSIT_REQUESTED = 'DEPOSIT_REQUESTED',
  // vakuus suoritettu
  DEPOSIT_PAID = 'DEPOSIT_PAID',
  // Aloitusvalvontapyyntö lähetetty
  PRELIMINARY_SUPERVISION_REQUESTED = 'PRELIMINARY_SUPERVISION_REQUESTED',
  // Aloitusvalvonta hylätty
  PRELIMINARY_SUPERVISION_REJECTED = 'PRELIMINARY_SUPERVISION_REJECTED',
  // Aloitusvalvonta suoritettu
  PRELIMINARY_SUPERVISION_DONE = 'PRELIMINARY_SUPERVISION_DONE',
  // Valvontapyyntö lähetetty
  SUPERVISION_REQUESTED = 'SUPERVISION_REQUESTED',
  // Valvonta hylätty
  SUPERVISION_REJECTED = 'SUPERVISION_REJECTED',
  // Valvonta suoritettu
  SUPERVISION_DONE = 'SUPERVISION_DONE',
  // odottaa. Hakemus odottaa lisätietoa, esimerkiksi selvitystä, mikä estää hakemuksen etenemisen
  WAITING = 'WAITING',
  // hyvitysselvitys. käytetään esim. hyvityslaskujen selvittämisen aikana
  COMPENSATION_CLARIFICATION = 'COMPENSATION_CLARIFICATION',
  // maksuperusteet korjattava
  PAYMENT_BASIS_CORRECTION = 'PAYMENT_BASIS_CORRECTION',
  // toiminnallinen kunto ilmoitettu
  OPERATIONAL_CONDITION_REPORTED = 'OPERATIONAL_CONDITION_REPORTED',
  // toiminnallinen kunto hylätty
  OPERATIONAL_CONDITION_REJECTED = 'OPERATIONAL_CONDITION_REJECTED',
  // toiminnallinen kunto hyväksytty
  OPERATIONAL_CONDITION_ACCEPTED = 'OPERATIONAL_CONDITION_ACCEPTED',
  // Loppuvalvontapyyntö lähetetty
  FINAL_SUPERVISION_REQUESTED = 'FINAL_SUPERVISION_REQUESTED',
  // Loppuvalvonta hylätty
  FINAL_SUPERVISION_REJECTED = 'FINAL_SUPERVISION_REJECTED',
  // Loppuvalvonta hyväksytty
  FINAL_SUPERVISION_ACCEPTED = 'FINAL_SUPERVISION_ACCEPTED',
  // laskutettavan sap-tunnus ei tiedossa
  SAP_ID_MISSING = 'SAP_ID_MISSING',
  // Päätös lähettämättä
  DECISION_NOT_SENT = 'DECISION_NOT_SENT',
  // Sopimusta ei hyväksytty
  CONTRACT_REJECTED = 'CONTRACT_REJECTED',
  // Aikamuutos
  DATE_CHANGE = 'DATE_CHANGE',
  // Muut muutokset
  OTHER_CHANGES = 'OTHER_CHANGES'
}

export const manuallyAddedTagTypes = [
  ApplicationTagType.WAITING,
  ApplicationTagType.ADDITIONAL_INFORMATION_REQUESTED,
  ApplicationTagType.STATEMENT_REQUESTED,
  ApplicationTagType.COMPENSATION_CLARIFICATION,
  ApplicationTagType.PAYMENT_BASIS_CORRECTION,
  ApplicationTagType.DECISION_NOT_SENT,
  ApplicationTagType.CONTRACT_REJECTED,
  ApplicationTagType.OTHER_CHANGES
];

export const removableTagTypes = manuallyAddedTagTypes.concat([
  ApplicationTagType.DATE_CHANGE
]);
