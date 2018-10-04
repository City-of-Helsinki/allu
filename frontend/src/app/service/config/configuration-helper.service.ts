import {Injectable} from '@angular/core';
import * as fromRoot from '@feature/allu/reducers';
import {Store} from '@ngrx/store';
import {Observable} from 'rxjs/internal/Observable';
import {combineLatest} from 'rxjs/internal/observable/combineLatest';
import {ConfigurationKey} from '@model/config/configuration-key';
import {map, take} from 'rxjs/operators';
import {TimeUtil} from '@util/time.util';

@Injectable()
export class ConfigurationHelperService {
  constructor(private store: Store<fromRoot.State>) {}

  public inWinterTime(date: Date): Observable<boolean> {
    return combineLatest(
      this.store.select(fromRoot.getConfiguration(ConfigurationKey.WINTER_TIME_START)),
      this.store.select(fromRoot.getConfiguration(ConfigurationKey.WINTER_TIME_END))
    ).pipe(
      take(1),
      map(([start, end]) => TimeUtil.isInWinterTime(date, start.value, end.value))
    );
  }
}
