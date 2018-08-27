import {ExternalUser} from '../../../model/common/external-user';

export class ExternalUserForm {
  constructor(
    public id?: number,
    public username?: string,
    public name?: string,
    public emailAddress?: string,
    public active?: boolean,
    public assignedRoles?: Array<string>,
    public password?: string,
    public expirationTime?: Date,
    public connectedCustomers?: Array<number>
  ) {}

  static to(form: ExternalUserForm): ExternalUser {
    const user = new ExternalUser();
    user.id = form.id;
    user.username = form.username;
    user.name = form.name;
    user.emailAddress = form.emailAddress;
    user.active = form.active;
    user.password = form.password;
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
      user.password,
      user.expirationTime,
      user.connectedCustomers
    );
  }
}
