import {Actions, Effect} from '@ngrx/effects';
import {Action, Store} from '@ngrx/store';
import {Injectable} from '@angular/core';
import {defer, Observable, of} from 'rxjs';
import {catchError, filter, map, switchMap} from 'rxjs/operators';
import * as fromAuth from '../../auth/reducers';
import {ConfigurationService} from '@service/config/configuration.service';
import {LoadFailed, LoadSuccess} from '../actions/configuration-actions';
import {Configuration, ConfigurationKeyMap} from '@model/config/configuration';

@Injectable()
export class ConfigurationEffects {
  constructor(private actions: Actions, private store: Store<fromAuth.State>, private configurationService: ConfigurationService) {}

  @Effect()
  init: Observable<Action> = defer(() => this.store.select(fromAuth.getLoggedIn).pipe(
    filter(loggedIn => loggedIn),
    switchMap(() => this.configurationService.getConfigurations().pipe(
      map(configurations => this.toConfigurationMap(configurations)),
      map(configurations => new LoadSuccess(configurations)),
      catchError(error => of(new LoadFailed(error))))
    ))
  );

  private toConfigurationMap(configurations: Configuration[]): ConfigurationKeyMap {
    return configurations.reduce((prev: ConfigurationKeyMap, cur) => {
      prev[cur.key] = cur;
      return prev;
    }, {});
  }
}
