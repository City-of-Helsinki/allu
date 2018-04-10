export class SupervisionTaskSearchCriteria {
  constructor(
    public taskTypes?: Array<string>,
    public applicationId?: string,
    public after?: Date,
    public before?: Date,
    public applicationTypes?: Array<string>,
    public applicationStatus?: Array<string>,
    public ownerId?: number,
    public cityDistrictIds?: Array<number>
  ) {}
}

export interface BackendSupervisionTaskSearchCriteria {
  taskTypes: Array<string>;
  applicationId: string;
  after: string;
  before: string;
  applicationTypes: Array<string>;
  applicationStatus: Array<string>;
  ownerId: number;
  cityDistrictIds: Array<number>;
}
