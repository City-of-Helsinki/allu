export const FieldKeyMapping = {
  START_TIME: {
    fieldName: 'startTime',
    valueField: 'startTime'
  },
  END_TIME: {
    fieldName: 'endTime',
    valueField: 'endTime'
  },
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
  startTime: 'startTime',
  endTime: 'endTime',
  identificationNumber: 'identificationNumber',
  workDescription: 'extension.additionalInfo',
  propertyIdentificationNumber: 'extension.propertyIdentificationNumber'
};
