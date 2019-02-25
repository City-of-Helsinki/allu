import {Actions, Effect} from '@ngrx/effects';
import {Action, Store} from '@ngrx/store';
import {Injectable} from '@angular/core';
import {Observable, of, defer} from 'rxjs';
import {catchError, filter, map, switchMap} from 'rxjs/operators';
import * as fromAuth from '@app/feature/auth/reducers';
import {LocationService} from '@service/location.service';
import {LoadSuccess} from '@feature/allu/actions/fixed-location-actions';
import {NotifyFailure} from '@feature/notification/actions/notification-actions';

@Injectable()
export class FixedLocationEffects {
  constructor(private actions: Actions, private store: Store<fromAuth.State>, private locationService: LocationService) {}

  @Effect()
  init: Observable<Action> = defer(() => this.store.select(fromAuth.getLoggedIn).pipe(
    filter(loggedIn => loggedIn),
    switchMap(() => this.locationService.getFixedLocations().pipe(
      map(fixedLocations => new LoadSuccess(fixedLocations)),
      catchError(error => of(new NotifyFailure(error))))
    ))
  );
}
