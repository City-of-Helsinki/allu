import {filter, withLatestFrom} from 'rxjs/operators';
import {NumberUtil} from '@util/number.util';
import {Action} from '@ngrx/store';
import {Observable} from 'rxjs/index';

export function withLatestExisting<T extends Action>(latestTarget: Observable<any>) {
  return (source: Observable<T>) => source.pipe(
    withLatestFrom(latestTarget),
    filter(([action, target]) => NumberUtil.isExisting(target))
  );
}
