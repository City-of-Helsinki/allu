import {Injectable} from '@angular/core';
import {UserService} from './user-service';
import {User} from '../../model/user/user';
import {Observable} from 'rxjs/Observable';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {NumberUtil} from '../../util/number.util';

@Injectable()
export class CurrentUser {

  private user$ = new BehaviorSubject<User>(undefined);

  constructor(private userService: UserService) {}

  clearUser(): void {
    this.user$.next(undefined);
  }

  get user(): Observable<User> {
    if (!this.user$.getValue()) {
      this.userService.getCurrentUser().subscribe(user => this.user$.next(user));
    }
    return this.user$.asObservable()
      .filter(u => !!u)
      .first(); // Use first so clients observable automatically completes after logged user is returned
  }

  public hasRole(roles: Array<string>): Observable<boolean> {
    return this.user
      .map(u => u.roles.reduce((prev, cur) => prev || roles.some(role => role === cur), false));
  }

  public hasApplicationType(types: Array<string>): Observable<boolean> {
    return this.user
      .map(u => u.allowedApplicationTypes.reduce((prev, cur) => prev || types.some(type => type === cur), false));
  }

  public isCurrentUser(id: number): Observable<boolean> {
    if (NumberUtil.isDefined(id)) {
      return this.user.map(user => user.id === id);
    } else {
      return Observable.of(false);
    }
  }
}
