import {BackendUser} from '../backend-model/backend-user';
import {User} from '../../model/common/user';
import {TimeUtil} from '../../util/time.util';

export class UserMapper {

  public static mapBackend(backendUser: BackendUser): User {
    return (backendUser) ?
      new User(
        backendUser.id,
        backendUser.userName,
        backendUser.realName,
        backendUser.emailAddress,
        backendUser.title,
        backendUser.active,
        TimeUtil.dateFromBackend(backendUser.lastLogin),
        backendUser.allowedApplicationTypes,
        backendUser.assignedRoles,
        backendUser.cityDistrictIds
        ) : undefined;
  }

  public static mapFrontend(user: User): BackendUser {
    return (user) ?
    {
      id: user.id,
      userName: user.userName,
      realName: user.realName,
      emailAddress: user.emailAddress,
      title: user.title,
      active: user.isActive,
      lastLogin: TimeUtil.dateToBackend(user.lastLogin),
      allowedApplicationTypes: user.allowedApplicationTypes,
      assignedRoles: user.assignedRoles,
      cityDistrictIds: user.cityDistrictIds
    } : undefined;
  }
}
