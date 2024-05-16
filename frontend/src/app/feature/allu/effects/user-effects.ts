import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {Action, select, Store} from '@ngrx/store';
import * as fromAuth from '@feature/auth/reducers';
import {Observable, of} from 'rxjs';
import {catchError, filter, map, switchMap, withLatestFrom} from 'rxjs/operators';
import {NotifyFailure} from '@feature/notification/actions/notification-actions';
import {UserService} from '@service/user/user-service';
import {Load, LoadSuccess, UserActionType} from '@feature/allu/actions/user-actions';

@Injectable()
export class UserEffects {
  constructor(private actions: Actions, private store: Store<fromAuth.State>, private userService: UserService) {}

  
  load: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<Load>(UserActionType.Load),
    withLatestFrom(this.store.pipe(select(fromAuth.getLoggedIn))),
    filter(([action, loggedIn]) => loggedIn),
    switchMap(() => this.userService.getAllUsers().pipe(
      map(users => new LoadSuccess(users)),
      catchError(error => of(new NotifyFailure(error))))
    )
  ));
}
