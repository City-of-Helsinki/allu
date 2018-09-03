export enum InformationRequestFieldKey {
  CUSTOMER,
  INVOICING_CUSTOMER,
  GEOMETRY,
  START_TIME,
  END_TIME,
  IDENTIFICATION_NUMBER,
  CLIENT_APPLICATION_KIND,
  APPLICATION_KIND,
  POSTAL_ADDRESS,
  WORK_DESCRIPTION,
  PROPERTY_IDENTIFICATION_NUMBER,
  ATTACHMENT
}

export const OtherInfoKeys: InformationRequestFieldKey[] = [
  InformationRequestFieldKey.START_TIME,
  InformationRequestFieldKey.END_TIME,
  InformationRequestFieldKey.IDENTIFICATION_NUMBER,
  InformationRequestFieldKey.WORK_DESCRIPTION,
  InformationRequestFieldKey.PROPERTY_IDENTIFICATION_NUMBER
];
