export enum AttachmentType {
  ADDED_BY_CUSTOMER = 'ADDED_BY_CUSTOMER',  // Asiakkaan lisäämä liite
  ADDED_BY_HANDLER = 'ADDED_BY_HANDLER', // Käsittelijän lisäämä liite
  DEFAULT = 'DEFAULT',          // Hakemustyyppikohtainen vakioliite
  DEFAULT_IMAGE = 'DEFAULT_IMAGE',    // Hakemustyyppikohtainen tyyppikuvaliite
  DEFAULT_TERMS = 'DEFAULT_TERMS',    // Hakemustyyppikohtainen ehtoliite
  SUPERVISION = 'SUPERVISION',      // Valvonnan liite
  STATEMENT = 'STATEMENT',        // Lausunto
  OTHER = 'OTHER'             // Muu liite
}

export const commonAttachmentTypes = [
  AttachmentType.ADDED_BY_CUSTOMER,
  AttachmentType.ADDED_BY_HANDLER,
  AttachmentType.SUPERVISION,
  AttachmentType.STATEMENT,
  AttachmentType.OTHER
];

export const isCommon = (type: AttachmentType) => commonAttachmentTypes.indexOf(type) >= 0;
