import {Injectable} from '@angular/core';
import {ExternalUserService} from './external-user-service';
import {ExternalUser} from '../../model/common/external-user';

@Injectable()
export class ExternalUserHub {
  constructor(private userService: ExternalUserService) {}

  /**
   * Fetches all external users
   */
  public getAllUsers = () => this.userService.getAllUsers();

  /**
   * Fetches single external user by id
   */
  public getUser = (id: number) => this.userService.getUser(id);

  /**
   * Saves given external user
   */
  public saveUser = (user: ExternalUser) => this.userService.save(user);
}
