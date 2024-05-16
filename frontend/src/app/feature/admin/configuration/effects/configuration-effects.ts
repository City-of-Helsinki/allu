import {Actions, createEffect, ofType} from '@ngrx/effects';
import {Action, select, Store} from '@ngrx/store';
import {Injectable} from '@angular/core';
import {defer, Observable, of} from 'rxjs';
import {catchError, filter, map, switchMap} from 'rxjs/operators';
import * as fromAuth from '@feature/auth/reducers';
import {ConfigurationService} from '@service/config/configuration.service';
import {ConfigurationActionType, LoadFailed, LoadSuccess, Save, SaveSuccess} from '../actions/configuration-actions';
import {NotifyFailure, NotifySuccess} from '@feature/notification/actions/notification-actions';
import {findTranslation} from '@util/translations';

@Injectable()
export class ConfigurationEffects {
  constructor(private actions: Actions, private store: Store<fromAuth.State>, private configurationService: ConfigurationService) {}

  
  init: Observable<Action> = createEffect(() => defer(() => this.store.pipe(
    select(fromAuth.getLoggedIn),
    filter(loggedIn => loggedIn),
    switchMap(() => this.configurationService.getConfigurations().pipe(
      map(configurations => new LoadSuccess(configurations)),
      catchError(error => of(new LoadFailed(error))))
    ))
  ));

  
  save: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<Save>(ConfigurationActionType.Save),
    switchMap(action => this.configurationService.save(action.payload).pipe(
      switchMap(saved => [
        new SaveSuccess(saved),
        new NotifySuccess(findTranslation('config.action.save'))
      ]),
      catchError(error => of(new NotifyFailure(error)))
    ))
  ));
}
