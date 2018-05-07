import {Injectable} from '@angular/core';
import {ErrorInfo} from '../../../service/error/error-info';
import {NotificationService} from '../../../service/notification/notification.service';
import {Actions} from '@ngrx/effects';
import {ProjectActionTypes} from '../../project/actions/project-actions';
import {
  ApplicationActionTypes as ProjectApplicationActionType
} from '../../project/actions/application-actions';
import {ActionWithPayload} from '../../common/action-with-payload';
import {ParentProjectActionType} from '../../project/actions/parent-project-actions';
import {ChildProjectActionType} from '../../project/actions/child-project-actions';
import {ApplicationBasketActionType} from '../../project/actions/application-basket-actions';

const handledActions = [
  ProjectActionTypes.LoadFailed,
  ProjectActionTypes.SaveFailed,
  ProjectApplicationActionType.LoadFailed,
  ProjectApplicationActionType.AddFailed,
  ProjectApplicationActionType.RemoveFailed,
  ParentProjectActionType.LoadFailed,
  ChildProjectActionType.LoadFailed,
  ApplicationBasketActionType.LoadFailed
];

@Injectable()
export class RootErrorNotificationService {
  constructor(private actions: Actions, private notification: NotificationService) {
    this.actions
      .ofType(...handledActions)
      .map((action: ActionWithPayload<ErrorInfo>) => action.payload)
      .subscribe(error => this.handle(error));
  }

  private handle(error: ErrorInfo): void {
    this.notification.errorInfo(error);
  }
}
