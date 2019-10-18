import {RoleType} from '@model/user/role-type';
import {ArrayUtil} from '@util/array-util';

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
  // laskutettavan sap-tunnus ei tiedossa
  SAP_ID_MISSING = 'SAP_ID_MISSING',
  // Päätös lähettämättä
  DECISION_NOT_SENT = 'DECISION_NOT_SENT',
  // Sopimusta ei hyväksytty
  CONTRACT_REJECTED = 'CONTRACT_REJECTED',
  // Aikamuutos
  DATE_CHANGE = 'DATE_CHANGE',
  // Kartoitettava
  SURVEY_REQUIRED = 'SURVEY_REQUIRED',
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
  ApplicationTagType.SURVEY_REQUIRED,
  ApplicationTagType.OTHER_CHANGES
];

export const removableTagTypes = manuallyAddedTagTypes.concat([
  ApplicationTagType.DATE_CHANGE
]);

export const DECISION_BLOCKING_TAGS = [
  ApplicationTagType.DATE_CHANGE,
  ApplicationTagType.OTHER_CHANGES
];

export function allowedTagsByRoles(roles: RoleType[]): ApplicationTagType[] {
  if (ArrayUtil.anyMatch(roles, [RoleType.ROLE_CREATE_APPLICATION, RoleType.ROLE_PROCESS_APPLICATION])) {
    return manuallyAddedTagTypes;
  } else if (ArrayUtil.anyMatch(roles, [RoleType.ROLE_MANAGE_SURVEY])) {
    return [ApplicationTagType.SURVEY_REQUIRED];
  } else {
    return [];
  }
}

export function removableTagsByRoles(roles: RoleType[]): ApplicationTagType[] {
  if (ArrayUtil.anyMatch(roles, [RoleType.ROLE_CREATE_APPLICATION, RoleType.ROLE_PROCESS_APPLICATION])) {
    return removableTagTypes;
  } else if (ArrayUtil.anyMatch(roles, [RoleType.ROLE_MANAGE_SURVEY])) {
    return [ApplicationTagType.SURVEY_REQUIRED];
  } else {
    return [];
  }
}
