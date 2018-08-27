import {TimeUtil} from '../../util/time.util';
import {BackendExternalUser} from '../backend-model/backend-external-user';
import {ExternalUser} from '../../model/common/external-user';
import {Some} from '../../util/option';
import {ExternalRoleType} from '../../model/common/external-role-type';

export class ExternalUserMapper {

  public static mapBackend(backendUser: BackendExternalUser): ExternalUser {
    return (backendUser) ?
      new ExternalUser(
        backendUser.id,
        backendUser.username,
        backendUser.name,
        backendUser.emailAddress,
        backendUser.active,
        TimeUtil.dateFromBackend(backendUser.lastLogin),
        Some(backendUser.assignedRoles)
          .map(roles => roles.map(role => ExternalRoleType[role]))
          .orElse([]),
        '',
        TimeUtil.dateFromBackend(backendUser.expirationTime),
        backendUser.connectedCustomers
      ) : undefined;
  }

  public static mapFrontend(user: ExternalUser): BackendExternalUser {
    return (user) ?
    {
      id: user.id,
      username: user.username,
      name: user.name,
      emailAddress: user.emailAddress,
      active: user.active,
      lastLogin: TimeUtil.dateToBackend(user.lastLogin),
      assignedRoles: user.uiAssignedRoles,
      password: user.password,
      expirationTime: TimeUtil.dateToBackend(user.expirationTime),
      connectedCustomers: user.connectedCustomers
    } : undefined;
  }
}
