export class TerminationInfo {
  constructor(
    public id?: number,
    public applicationId?: number,
    public creationTime?: Date,
    public terminationTime?: Date,
    public reason?: string
  ) {}
}
