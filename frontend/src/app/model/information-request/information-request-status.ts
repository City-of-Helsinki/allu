export enum InformationRequestStatus {
  DRAFT = 'DRAFT',
  OPEN = 'OPEN',
  RESPONSE_RECEIVED = 'RESPONSE_RECEIVED',
  CLOSED = 'CLOSED'
}

export const canHaveResponse = (status: InformationRequestStatus) => {
  return InformationRequestStatus.RESPONSE_RECEIVED === status
    || InformationRequestStatus.CLOSED === status;
};

export const isRequestActionable = (status: InformationRequestStatus) =>
  InformationRequestStatus.DRAFT === status
  || InformationRequestStatus.OPEN === status;

export const isRequestUnfinished = (status: InformationRequestStatus) => InformationRequestStatus.CLOSED !== status;
