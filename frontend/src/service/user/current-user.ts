import {Injectable} from '@angular/core';
import {JwtHelper} from 'angular2-jwt/angular2-jwt';
import {Some} from '../../util/option';
import {Option} from '../../util/option';
import {UserService} from './user-service';
import {User} from '../../model/common/user';
import {Observable} from 'rxjs/Observable';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';

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

  private userNameFromToken(token: string): string {
    const decoded = new JwtHelper().decodeToken(token);
    if (decoded) {
      return decoded['sub'];
    } else {
      throw new Error('No username in jwt token');
    }
  }
}
