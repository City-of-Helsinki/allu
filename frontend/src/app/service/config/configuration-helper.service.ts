import {Injectable} from '@angular/core';
import * as fromRoot from '@feature/allu/reducers';
import {select, Store} from '@ngrx/store';
import {Observable} from 'rxjs/internal/Observable';
import {combineLatest} from 'rxjs/internal/observable/combineLatest';
import {ConfigurationKey} from '@model/config/configuration-key';
import {filter, map, take} from 'rxjs/operators';
import {TimeUtil} from '@util/time.util';
import {Configuration} from '@model/config/configuration';
import {ApplicationKind} from '@model/application/type/application-kind';
import {of} from 'rxjs/internal/observable/of';
import {TimePeriod} from '@feature/application/info/time-period';

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

  public getTimePeriod(startConfig: ConfigurationKey, endConfig: ConfigurationKey): Observable<TimePeriod> {
    return combineLatest(
      this.getSingleConfiguration(startConfig),
      this.getSingleConfiguration(endConfig)
    ).pipe(
      take(1),
      filter(([start, end]) => !!start && !!end),
      map(([start, end]) => new TimePeriod(TimeUtil.dateFromBackend(start.value), TimeUtil.dateFromBackend(end.value)))
    );
  }

  public inWinterTime(date: Date): Observable<boolean> {
    return this.getTimePeriod(ConfigurationKey.WINTER_TIME_START, ConfigurationKey.WINTER_TIME_END).pipe(
      take(1),
      map((period) => TimeUtil.isInTimePeriod(date, period.startTime, period.endTime))
    );
  }

  public getTimePeriodForKind(kind: ApplicationKind): Observable<TimePeriod> {
    switch (kind) {
      case ApplicationKind.SUMMER_TERRACE:
        return this.getTimePeriod(ConfigurationKey.SUMMER_TERRACE_TIME_START, ConfigurationKey.SUMMER_TERRACE_TIME_END);
      case ApplicationKind.WINTER_TERRACE:
        return this.getTimePeriod(ConfigurationKey.WINTER_TERRACE_TIME_START, ConfigurationKey.WINTER_TERRACE_TIME_END);
      case ApplicationKind.PARKLET:
        return this.getTimePeriod(ConfigurationKey.PARKLET_TIME_START, ConfigurationKey.PARKLET_TIME_END);
      default:
        return of(undefined);
    }
  }
}
