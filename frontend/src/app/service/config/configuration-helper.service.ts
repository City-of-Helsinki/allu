import {Injectable} from '@angular/core';
import * as fromRoot from '@feature/allu/reducers';
import {select, Store} from '@ngrx/store';
import {Observable} from 'rxjs/internal/Observable';
import {combineLatest} from 'rxjs/internal/observable/combineLatest';
import {ConfigurationKey} from '@model/config/configuration-key';
import {map, take} from 'rxjs/operators';
import {TimeUtil} from '@util/time.util';
import {Configuration} from '@model/config/configuration';

@Injectable()
export class ConfigurationHelperService {
  constructor(private store: Store<fromRoot.State>) {}

  public getConfiguration(key: ConfigurationKey): Observable<Configuration[]> {
    return this.store.pipe(
      select(fromRoot.getAllConfigurations),
      map(configurations => configurations.filter(c => c.key === key))
    );
  }

  public getSingleConfiguration(key: ConfigurationKey): Observable<Configuration> {
    return this.getConfiguration(key).pipe(
      map(configurations => {
        if (configurations.length === 1) {
          return configurations[0];
        } else {
          throw new Error(`Expected single configuration with key ${key}, got ${configurations.length}`);
        }
      })
    );
  }

  public inWinterTime(date: Date): Observable<boolean> {
    return combineLatest(
      this.getSingleConfiguration(ConfigurationKey.WINTER_TIME_START),
      this.getSingleConfiguration(ConfigurationKey.WINTER_TIME_END)
    ).pipe(
      take(1),
      map(([start, end]) => TimeUtil.isInWinterTime(date, start.value, end.value))
    );
  }
}
