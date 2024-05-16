import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {Action, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {LocationService} from '@service/location.service';
import {Observable, of} from 'rxjs';
import {
  AddressSearchActionType,
  FetchCoordinates,
  FetchCoordinatesSuccess,
  Search,
  SearchSuccess
} from '@feature/map/actions/address-search-actions';
import {catchError, filter, map, switchMap} from 'rxjs/operators';
import {NotifyFailure} from '@feature/notification/actions/notification-actions';

@Injectable()
export class AddressSearchEffects {
  constructor(private actions: Actions,
              private store: Store<fromRoot.State>,
              private locationService: LocationService) {}

  
  search: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<Search>(AddressSearchActionType.Search),
    switchMap(action => this.locationService.search(action.payload).pipe(
      map(addresses => new SearchSuccess(addresses)),
      catchError(error => of(new NotifyFailure(error)))
    ))
  ));

  
  fetchCoordinates: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<FetchCoordinates>(AddressSearchActionType.FetchCoordinates),
    filter(action => !!action.payload),
    switchMap(action => this.locationService.geocode(action.payload).pipe(
      map(coordinatesOpt => coordinatesOpt
        .map(coordinates => new FetchCoordinatesSuccess(coordinates))
        .orElseGet(() => new FetchCoordinatesSuccess(undefined))),
      catchError(error => of(new NotifyFailure(error)))
    ))
  ));
}
