import {Actions, createEffect} from '@ngrx/effects';
import {Action, Store} from '@ngrx/store';
import {Injectable} from '@angular/core';
import {Observable, of, defer} from 'rxjs';
import {LoadFailed, LoadSuccess} from '../actions/city-district-actions';
import {catchError, filter, map, switchMap} from 'rxjs/operators';
import * as fromAuth from '../../auth/reducers';
import {LocationService} from '../../../service/location.service';
import {ArrayUtil} from '../../../util/array-util';
import {CityDistrict} from '../../../model/common/city-district';

@Injectable()
export class CityDistrictEffects {
  constructor(private actions: Actions, private store: Store<fromAuth.State>, private locationService: LocationService) {}

  
  init: Observable<Action> = createEffect(() => defer(() => this.store.select(fromAuth.getLoggedIn).pipe(
    filter(loggedIn => loggedIn),
    switchMap(() => this.locationService.districts().pipe(
      map(districts => this.sort(districts)),
      map(districts => new LoadSuccess(districts)),
      catchError(error => of(new LoadFailed(error))))
    ))
  ));

  private sort(districts: CityDistrict[]): CityDistrict[] {
    return districts.filter(d => d.districtId !== 0) // Ignore 0 Aluemeri
      .sort(ArrayUtil.naturalSort((district: CityDistrict) => district.name));
  }
}
