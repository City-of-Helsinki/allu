export const FieldKeyMapping = {
  IDENTIFICATION_NUMBER: {
    fieldName: 'identificationNumber',
    valueField: 'identificationNumber'
  },
  WORK_DESCRIPTION: {
    fieldName: 'workDescription',
    valueField: 'extension.additionalInfo'
  },
  PROPERTY_IDENTIFICATION_NUMBER: {
    fieldName: 'propertyIdentificationNumber',
    valueField: 'extension.propertyIdentificationNumber'
  }
};

export const FieldNameMapping = {
  identificationNumber: 'identificationNumber',
  workDescription: 'extension.additionalInfo',
  propertyIdentificationNumber: 'extension.propertyIdentificationNumber'
};
