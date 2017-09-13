import {ExternalUser} from '../../../model/common/external-user';

export class ExternalUserForm {
  constructor(
    public id?: number,
    public username?: string,
    public name?: string,
    public emailAddress?: string,
    public active?: boolean,
    public assignedRoles?: Array<string>,
    public token?: string,
    public expirationTime?: Date,
    public connectedCustomers?: Array<number>
  ) {}

  static to(form: ExternalUserForm): ExternalUser {
    let user = new ExternalUser();
    user.id = form.id;
    user.username = form.username;
    user.name = form.name;
    user.emailAddress = form.emailAddress;
    user.active = form.active;
    user.token = form.token;
    user.uiAssignedRoles = form.assignedRoles;
    user.expirationTime = form.expirationTime;
    user.connectedCustomers = form.connectedCustomers;
    return user;
  }

  static from(user: ExternalUser): ExternalUserForm {
    return new ExternalUserForm(
      user.id,
      user.username,
      user.name,
      user.emailAddress,
      user.active,
      user.uiAssignedRoles,
      user.token,
      user.expirationTime,
      user.connectedCustomers
    );
  }
}
