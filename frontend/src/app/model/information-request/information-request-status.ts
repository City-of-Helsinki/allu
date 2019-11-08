export enum InformationRequestStatus {
  DRAFT = 'DRAFT',
  OPEN = 'OPEN',
  RESPONSE_RECEIVED = 'RESPONSE_RECEIVED',
  CLOSED = 'CLOSED'
}

export const hasResponse = (status: InformationRequestStatus) => {
  return InformationRequestStatus.RESPONSE_RECEIVED === status
    || InformationRequestStatus.CLOSED === status;
};
