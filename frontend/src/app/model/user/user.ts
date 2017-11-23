import {TimeUtil} from '../../util/time.util';
export class User {
  constructor(
    public id?: number,
    public userName?: string,
    public realName?: string,
    public emailAddress?: string,
    public title?: string,
    public isActive?: boolean,
    public lastLogin?: Date,
    public allowedApplicationTypes: Array<string> = [],
    public assignedRoles: Array<string> = [],
    public cityDistrictIds: Array<number> = []) {}

  hasRole(role: string): boolean {
    return this.assignedRoles.indexOf(role) >= 0;
  }

  get isAdmin(): boolean {
    return this.hasRole('ROLE_ADMIN');
  }

  get roles(): Array<string> {
    return this.assignedRoles;
  }

  get uiLastLogin(): string {
    return TimeUtil.getUiDateTimeString(this.lastLogin);
  }
}
