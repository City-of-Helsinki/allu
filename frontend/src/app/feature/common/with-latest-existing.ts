import {filter, withLatestFrom} from 'rxjs/operators';
import {NumberUtil} from '@util/number.util';
import {Observable, OperatorFunction, ObservableInput} from 'rxjs';

export function withLatestExisting<T, T2>(latestTarget: ObservableInput<T2>): OperatorFunction<T, [T, T2]> {
  return (source: Observable<T>) => source.pipe(
    withLatestFrom(latestTarget),
    filter(([action, target]) => NumberUtil.isExisting(target))
  );
}
