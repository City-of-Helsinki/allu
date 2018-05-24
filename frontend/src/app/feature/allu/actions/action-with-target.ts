import {Action} from '@ngrx/store';
import {ActionTargetType} from './action-target-type';
import {filter, withLatestFrom} from 'rxjs/operators';
import {ofType} from '@ngrx/effects';
import {Observable} from 'rxjs';

export interface ActionWithTarget extends Action {
  targetType: ActionTargetType;
}

export function ofTargetAndType<T extends ActionWithTarget>(targetType: ActionTargetType,
                                                            latestTarget: Observable<any>,
                                                            ...allowedTypes: string[]) {
  return (source: Observable<ActionWithTarget>) => source.pipe(
    ofType<T>(...allowedTypes),
    filter(action => targetType === action.targetType),
    withLatestFrom(latestTarget),
    filter(([action, target]) => target.id !== undefined)
  );
}


