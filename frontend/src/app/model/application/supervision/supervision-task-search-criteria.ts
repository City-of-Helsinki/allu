import {SupervisionTaskType} from './supervision-task-type';
import {ApplicationType} from '../type/application-type';
import {ApplicationStatus} from '../application-status';

export class SupervisionTaskSearchCriteria {
  constructor(
    public taskTypes?: Array<SupervisionTaskType>,
    public applicationId?: string,
    public after?: Date,
    public before?: Date,
    public applicationTypes?: Array<ApplicationType>,
    public applicationStatus?: Array<ApplicationStatus>,
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
