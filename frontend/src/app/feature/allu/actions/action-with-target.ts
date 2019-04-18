import {Action} from '@ngrx/store';
import {ActionTargetType} from './action-target-type';
import {filter, withLatestFrom} from 'rxjs/operators';
import {ofType} from '@ngrx/effects';
import {Observable} from 'rxjs';
import {NumberUtil} from '../../../util/number.util';

export interface ActionWithTarget extends Action {
  targetType: ActionTargetType;
}

export function ofTargetAndType<T extends ActionWithTarget>(targetType: ActionTargetType,
                                                        latestTarget: Observable<any>,
                                                        ...allowedTypes: string[]) {
  return (source: Observable<T>) => fromSource<T>(source, targetType, latestTarget, ...allowedTypes);
}

export function ofExistingTargetAndType<T extends ActionWithTarget>(targetType: ActionTargetType,
                                                                    latestTarget: Observable<any>,
                                                                    ...allowedTypes: string[]) {
  return (source: Observable<T>) => fromSource<T>(source, targetType, latestTarget, ...allowedTypes).pipe(
    filter(([action, target]) => NumberUtil.isExisting(target))
  );
}

function fromSource<T extends ActionWithTarget>(source: Observable<T>,
                    targetType: ActionTargetType,
                    latestTarget: Observable<any>,
                    ...allowedTypes: string[]) {
  return source.pipe(
    ofType<T>(...allowedTypes),
    filter(action => targetType === action.targetType),
    withLatestFrom(latestTarget)
  );
}


