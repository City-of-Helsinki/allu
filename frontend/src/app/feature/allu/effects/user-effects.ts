import {Injectable} from '@angular/core';
import {Actions, Effect} from '@ngrx/effects';
import {Action, select, Store} from '@ngrx/store';
import * as fromAuth from '@feature/auth/reducers';
import {defer, Observable, of} from 'rxjs';
import {catchError, filter, map, switchMap} from 'rxjs/operators';
import {NotifyFailure} from '@feature/notification/actions/notification-actions';
import {UserService} from '@service/user/user-service';
import {LoadSuccess} from '@feature/allu/actions/user-actions';

@Injectable()
export class UserEffects {
  constructor(private actions: Actions, private store: Store<fromAuth.State>, private userService: UserService) {}

  @Effect()
  init: Observable<Action> = defer(() => this.store.pipe(
    select(fromAuth.getLoggedIn),
    filter(loggedIn => loggedIn),
    switchMap(() => this.userService.getAllUsers().pipe(
      map(users => new LoadSuccess(users)),
      catchError(error => of(new NotifyFailure(error))))
    ))
  );
}
