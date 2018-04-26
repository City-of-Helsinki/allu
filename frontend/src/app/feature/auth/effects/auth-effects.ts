import {Injectable} from '@angular/core';
import {CurrentUser} from '../../../service/user/current-user';
import {Actions, Effect, ofType} from '@ngrx/effects';
import {Observable} from 'rxjs/Observable';
import {Action} from '@ngrx/store';
import {AuthActionType, LoggedIn, LoggedUserLoaded} from '../actions/auth-actions';
import {filter, map, switchMap} from 'rxjs/operators';
import {defer} from 'rxjs/observable/defer';
import {AuthService} from '../../../service/authorization/auth.service';
import {of} from 'rxjs/observable/of';

@Injectable()
export class AuthEffects {

  constructor(private actions: Actions,
              private currentUser: CurrentUser,
              private authService: AuthService) {}

  @Effect()
  loadLoggedUser: Observable<Action> = this.actions.pipe(
    ofType<LoggedIn>(AuthActionType.LoggedIn),
    switchMap(() => this.currentUser.user.pipe(
      map(user => new LoggedUserLoaded(user))
    ))
  );

  @Effect()
  authenticated: Observable<Action> = defer(() => of(this.authService.authenticated())).pipe(
    filter(loggedIn => loggedIn),
    map(() => new LoggedIn())
  );
}
