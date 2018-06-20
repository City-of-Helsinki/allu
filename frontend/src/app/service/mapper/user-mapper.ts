import {BackendUser, SearchResultUser} from '../backend-model/backend-user';
import {User} from '../../model/user/user';
import {TimeUtil} from '../../util/time.util';
import {UserSearchCriteria} from '../../model/user/user-search-criteria';

export class UserMapper {

  public static mapSearchResult(backendUser: SearchResultUser): User {
    if (backendUser) {
      const user = new User();
      user.userName = backendUser.userName;
      user.realName = backendUser.realName;
      return user;
    } else {
      return undefined;
    }
  }

  public static mapBackend(backendUser: BackendUser): User {
    return (backendUser) ?
      new User(
        backendUser.id,
        backendUser.userName,
        backendUser.realName,
        backendUser.emailAddress,
        backendUser.phone,
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
      phone: user.phone,
      title: user.title,
      active: user.isActive,
      lastLogin: TimeUtil.dateToBackend(user.lastLogin),
      allowedApplicationTypes: user.allowedApplicationTypes,
      assignedRoles: user.assignedRoles,
      cityDistrictIds: user.cityDistrictIds
    } : undefined;
  }

  public static mapSearchCriteria(searchCriteria: UserSearchCriteria): any {
    return {
      roleType: searchCriteria.uiRoleType,
      applicationType: searchCriteria.uiApplicationType,
      cityDistrictId: searchCriteria.cityDistrictId
    };
  }
}
