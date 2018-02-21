import {Injectable} from '@angular/core';
import {UserService} from './user-service';
import {User} from '../../model/user/user';
import {RoleType} from '../../model/user/role-type';
import {UserSearchCriteria} from '../../model/user/user-search-criteria';

@Injectable()
export class UserHub {
  constructor(private userService: UserService) {}

  /**
   * Fetches all active users.
   */
  public getActiveUsers = () => this.userService.getActiveUsers();

  /**
   * Searches users by given criteria
   * @param {UserSearchCriteria} criteria
   */
  public searchUsers = (criteria: UserSearchCriteria) => this.userService.search(criteria);

  /**
   * Fetches customers which have give role
   */
  public getByRole = (role: RoleType) => this.userService.getByRole(role);

  /**
   * Fetches all users
   */
  public getAllUsers = () => this.userService.getAllUsers();

  /**
   * Fetches single user by username
   */
  public getByUsername = (userName: string) => this.userService.getByUsername(userName);

  /**
   * Fetches single user by id
   */
  public getById = (id: number) => this.userService.getById(id);

  /**
   * Fetches currently logged user
   */
  public getCurrent = () => this.userService.getCurrentUser();

  /**
   * Saves given user
   */
  public saveUser = (user: User) => this.userService.save(user);
}
