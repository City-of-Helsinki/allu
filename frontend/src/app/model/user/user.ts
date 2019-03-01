import {ApplicationType} from '@app/model/application/type/application-type';
import {RoleType} from '@app/model/user/role-type';

export class User {
  constructor(
    public id?: number,
    public userName?: string,
    public realName?: string,
    public emailAddress?: string,
    public phone?: string,
    public title?: string,
    public isActive?: boolean,
    public lastLogin?: Date,
    public allowedApplicationTypes: Array<ApplicationType> = [],
    public assignedRoles: Array<RoleType> = [],
    public cityDistrictIds: Array<number> = []) {}

  hasRole(role: RoleType): boolean {
    return this.assignedRoles.indexOf(role) >= 0;
  }

  get isAdmin(): boolean {
    return this.hasRole(RoleType.ROLE_ADMIN);
  }

  get roles(): Array<RoleType> {
    return this.assignedRoles;
  }
}
