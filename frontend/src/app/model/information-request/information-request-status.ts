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
