import {Actions, Effect} from '@ngrx/effects';
import {Action, select, Store} from '@ngrx/store';
import {Injectable} from '@angular/core';
import {defer, Observable, of} from 'rxjs';
import {catchError, filter, map, switchMap} from 'rxjs/operators';
import * as fromAuth from '../../auth/reducers';
import {ConfigurationService} from '@service/config/configuration.service';
import {LoadFailed, LoadSuccess} from '../actions/configuration-actions';

@Injectable()
export class ConfigurationEffects {
  constructor(private actions: Actions, private store: Store<fromAuth.State>, private configurationService: ConfigurationService) {}

  @Effect()
  init: Observable<Action> = defer(() => this.store.pipe(
    select(fromAuth.getLoggedIn),
    filter(loggedIn => loggedIn),
    switchMap(() => this.configurationService.getConfigurations().pipe(
      map(configurations => new LoadSuccess(configurations)),
      catchError(error => of(new LoadFailed(error))))
    ))
  );
}
