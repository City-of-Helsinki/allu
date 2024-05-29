import {Injectable} from '@angular/core';
import {UserService} from './user-service';
import {User} from '../../model/user/user';
import {Observable, BehaviorSubject, of} from 'rxjs';
import {NumberUtil} from '../../util/number.util';
import {filter, first, map} from 'rxjs/internal/operators';
import { RoleType } from '@app/model/user/role-type';

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

    // Use first so clients observable automatically completes after logged user is returned
    return this.user$.asObservable().pipe(
      filter(u => !!u),
      first()
    );
  }

  public hasRole(roles: Array<string>): Observable<boolean> {
    return this.user.pipe(
      map(u => u.roles.reduce((prev, cur) => prev || roles.some(role => role === cur), false))
    );
  }

  public hasOnlyView(): Observable<boolean> {
    return this.user.pipe(
      map(u => u.roles.length === 1 && u.roles.includes(RoleType.ROLE_VIEW))
    );
  }

  public hasApplicationType(types: Array<string>): Observable<boolean> {
    return this.user.pipe(
      map(u => u.allowedApplicationTypes.reduce((prev, cur) => prev || types.some(type => type === cur), false))
    );
  }

  public isCurrentUser(id: number): Observable<boolean> {
    if (NumberUtil.isDefined(id)) {
      return this.user.pipe(map(user => user.id === id));
    } else {
      return of(false);
    }
  }
}
