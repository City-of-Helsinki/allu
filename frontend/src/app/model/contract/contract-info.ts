export class ContractInfo {
  constructor(
    public id?: number,
    public applicationId?: number,
    public status?: string,
    public rejectionReason?: string,
    public creationTime?: Date,
    public responseTime?: Date,
    public signer?: string,
    public frameAgreementExists?: boolean,
    public contractAsAttachment?: boolean
) {
  }
}
