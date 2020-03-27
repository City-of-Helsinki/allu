import {TimeUtil} from '@util/time.util';

export class SupervisionTaskSearchCriteria {
  constructor(
    public taskTypes?: Array<string>,
    public applicationId?: string,
    public after?: Date,
    public before?: Date,
    public applicationTypes?: Array<string>,
    public applicationStatus?: Array<string>,
    public owners?: Array<number>,
    public cityDistrictIds?: Array<number>
  ) {}

  public static updateDatesForSearch(search: SupervisionTaskSearchCriteria): SupervisionTaskSearchCriteria {
    const query = new SupervisionTaskSearchCriteria();
    query.taskTypes = search.taskTypes;
    query.applicationId = search.applicationId;
    query.after = TimeUtil.toStartDate(search.after);
    query.before = TimeUtil.toEndDate(search.before);
    query.applicationTypes = search.applicationTypes;
    query.applicationStatus = search.applicationStatus;
    query.owners = search.owners;
    query.cityDistrictIds = search.cityDistrictIds;
    return query;
  }
}

export interface BackendSupervisionTaskSearchCriteria {
  taskTypes: Array<string>;
  applicationId: string;
  after: string;
  before: string;
  applicationTypes: Array<string>;
  applicationStatus: Array<string>;
  owners: Array<number>;
  cityDistrictIds: Array<number>;
}
