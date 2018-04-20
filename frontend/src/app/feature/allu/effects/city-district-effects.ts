import {Effect} from '@ngrx/effects';
import {Action} from '@ngrx/store';
import {Injectable} from '@angular/core';
import {CityDistrictService} from '../../../service/map/city-district.service';
import {Observable} from 'rxjs/Observable';
import {LoadSuccess} from '../actions/city-district-actions';
import {map} from 'rxjs/operators';
import {defer} from 'rxjs/observable/defer';

@Injectable()
export class CityDistrictEffects {
  constructor(private cityDistrictService: CityDistrictService) {}

  @Effect()
  init: Observable<Action> = defer(() => this.cityDistrictService.get()).pipe(
    map(districts => new LoadSuccess(districts))
  );
}
