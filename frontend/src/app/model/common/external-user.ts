import {TimeUtil} from '../../util/time.util';
import {ExternalRoleType} from './external-role-type';

export class ExternalUser {
  constructor(
    public id?: number,
    public username?: string,
    public name?: string,
    public emailAddress?: string,
    public active?: boolean,
    public lastLogin?: Date,
    public assignedRoles: Array<ExternalRoleType> = [],
    public token?: string,
    public expirationTime?: Date,
    public connectedCustomers?: Array<number>) {}

  get uiLastLogin(): string {
    return TimeUtil.getUiDateTimeString(this.lastLogin);
  }

  get uiExpirationTime() {
    return TimeUtil.getUiDateString(this.expirationTime);
  }

  get uiAssignedRoles() {
    return this.assignedRoles.map(role => ExternalRoleType[role]);
  }

  set uiAssignedRoles(roles: Array<string>) {
    this.assignedRoles = roles.map(role => ExternalRoleType[role]);
  }
}
