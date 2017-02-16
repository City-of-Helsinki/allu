export enum AttachmentType {
  ADDED_BY_CUSTOMER,  // Asiakkaan lisäämä liite
  ADDED_BY_HANDLER, // Käsittelijän lisäämä liite
  DEFAULT,          // Hakemustyyppikohtainen vakioliite
  DEFAULT_IMAGE,    // Hakemustyyppikohtainen tyyppikuvaliite
  DEFAULT_TERMS     // Hakemustyyppikohtainen ehtoliite
}

export const commonAttachmentTypes = [
  AttachmentType.ADDED_BY_CUSTOMER,
  AttachmentType.ADDED_BY_HANDLER
];

export const isCommon = (type: string) => commonAttachmentTypes.indexOf(AttachmentType[type]) >= 0;
