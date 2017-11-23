import {RoleType} from './role-type';
import {ApplicationType} from '../application/type/application-type';

export class UserSearchCriteria {
  constructor(
    public roleType?: RoleType,
    public applicationType?: ApplicationType,
    public cityDistrictId?: number
  ) {}

  get uiRoleType() {
    return RoleType[this.roleType];
  }

  get uiApplicationType() {
    return ApplicationType[this.applicationType];
  }
}
