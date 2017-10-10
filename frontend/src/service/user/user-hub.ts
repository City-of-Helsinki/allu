import {Injectable} from '@angular/core';
import {UserService} from './user-service';
import {User} from '../../model/user/user';
import {RoleType} from '../../model/user/role-type';

@Injectable()
export class UserHub {
  constructor(private userService: UserService) {}

  /**
   * Fetches all active users.
   */
  public getActiveUsers = () => this.userService.getActiveUsers();

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
  public getUser = (userName: string) => this.userService.getUser(userName);

  /**
   * Fetches currently logged user
   */
  public getCurrent = () => this.userService.getCurrentUser();

  /**
   * Saves given user
   */
  public saveUser = (user: User) => this.userService.save(user);
}
