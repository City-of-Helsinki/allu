import {Injectable} from '@angular/core';
import {UserService} from './user-service';
import {Observable} from 'rxjs/Observable';

@Injectable()
export class UserHub {
  constructor(private userService: UserService) {}

  /**
   * Fetches all active users.
   */
  public getActiveUsers = () => this.userService.getActiveUsers();
}
