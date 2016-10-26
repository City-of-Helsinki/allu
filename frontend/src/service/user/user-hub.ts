import {Injectable} from '@angular/core';
import {UserService} from './user-service';
import {Observable} from 'rxjs/Observable';
import {User} from '../../model/common/user';

@Injectable()
export class UserHub {
  constructor(private userService: UserService) {}

  /**
   * Fetches all active users.
   */
  public getActiveUsers = () => this.userService.getActiveUsers();


  /**
   * Fetches all users
   */
  public getAllUsers = () => this.userService.getAllUsers();

  /**
   * Fetches single user by username
   */
  public getUser = (userName: string) => this.userService.getUser(userName);

  /**
   * Saves given user
   */
  public saveUser = (user: User) => this.userService.save(user);
}
