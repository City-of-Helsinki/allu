import {Application} from '@model/application/application';
import {ErrorInfo} from '@service/error/error-info';
import {ActionWithPayload} from '@feature/common/action-with-payload';
import {ActionWithTarget} from '@feature/allu/actions/action-with-target';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';

export enum ApplicationSearchActionType {
  SearchByNameOrId = '[ApplicationSearch] Search applications',
  SearchByNameOrIdSuccess = '[ApplicationSearch] Search applications success',
  SearchByNameOrIdFailed = '[ApplicationSearch] Search applications failed'
}

export class SearchByNameOrId implements ActionWithTarget {
  readonly type = ApplicationSearchActionType.SearchByNameOrId;

  constructor(public targetType: ActionTargetType, public payload: string) {}
}

export class SearchByNameOrIdSuccess implements ActionWithTarget {
  readonly type = ApplicationSearchActionType.SearchByNameOrIdSuccess;

  constructor(public targetType: ActionTargetType, public payload: Application[]) {}
}

export class SearchByNameOrIdFailed implements ActionWithTarget, ActionWithPayload<ErrorInfo> {
  readonly type = ApplicationSearchActionType.SearchByNameOrIdFailed;

  constructor(public targetType: ActionTargetType, public payload: ErrorInfo) {}
}

export type ApplicationSearchActions =
  | SearchByNameOrId
  | SearchByNameOrIdSuccess
  | SearchByNameOrIdFailed;
