import {SupervisionTaskType} from './supervision-task-type';
import {ApplicationStatus} from '../application-status';
import {User} from '../../user/user';
import {PostalAddress} from '../../common/postal-address';
import {BackendUser} from '@service/backend-model/backend-user';

export class SupervisionWorkItem {
  constructor(
    public id?: number,
    public type?: SupervisionTaskType ,
    public applicationId?: number,
    public applicationIdText?: string,
    public applicationStatus?: ApplicationStatus,
    public creator?: User,
    public plannedFinishingTime?: Date,
    public postalAddress?: PostalAddress,
    public address?: string,
    public projectName?: string,
    public owner?: User
  ) {}

  get uiType(): string {
     return SupervisionTaskType[this.type];
  }

  get uiApplicationStatus(): string {
     return ApplicationStatus[this.applicationStatus];
  }
}

export interface BackendSupervisionWorkItem {
   id: number;
   type: string;
   applicationId: number;
   applicationIdText: string;
   applicationStatus: string;
   creator: BackendUser;
   plannedFinishingTime: string;
   postalAddress: PostalAddress;
   address: string;
   projectName: string;
   owner: BackendUser;
}
