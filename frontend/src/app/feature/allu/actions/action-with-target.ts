import {Action} from '@ngrx/store';
import {ActionTargetType} from './action-target-type';
import {filter, withLatestFrom} from 'rxjs/operators';
import {ofType} from '@ngrx/effects';
import {Observable} from 'rxjs';
import {NumberUtil} from '../../../util/number.util';

export interface ActionWithTarget extends Action {
  targetType: ActionTargetType;
}

export function withLatestOfTargetAndType<T extends ActionWithTarget>(targetType: ActionTargetType,
                                                                      latestTarget: Observable<any>,
                                                                      ...allowedTypes: string[]) {
  return (source: Observable<T>) => latestFromSource<T>(source, targetType, latestTarget, ...allowedTypes);
}

export function withLatestExistingOfTargetAndType<T extends ActionWithTarget>(targetType: ActionTargetType,
                                                                              latestTarget: Observable<any>,
                                                                              ...allowedTypes: string[]) {
  return (source: Observable<T>) => latestFromSource<T>(source, targetType, latestTarget, ...allowedTypes).pipe(
    filter(([action, target]) => NumberUtil.isExisting(target))
  );
}

export function ofTargetAndType<T extends ActionWithTarget>(targetType: ActionTargetType, ...allowedTypes: string[]) {
  return (source: Observable<T>) => fromSource(source, targetType, ...allowedTypes);
}

function fromSource<T extends ActionWithTarget>(
  source: Observable<T>, targetType: ActionTargetType, ...allowedTypes: string[]) {
  return source.pipe(
    ofType<T>(...allowedTypes),
    filter(action => targetType === action.targetType)
  );
}

function latestFromSource<T extends ActionWithTarget>(
  source: Observable<T>, targetType: ActionTargetType, latestTarget: Observable<any>, ...allowedTypes: string[]) {
  return fromSource(source, targetType, ...allowedTypes).pipe(withLatestFrom(latestTarget));
}
