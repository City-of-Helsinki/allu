export enum ApprovalDocumentType {
  OPERATIONAL_CONDITION = 'OPERATIONAL_CONDITION',
  WORK_FINISHED = 'WORK_FINISHED'
}

export class ApprovalDocument {
  constructor(
    public type?: ApprovalDocumentType,
    public applicationId?: number,
    public pdf?: Blob
  ) {}
}
