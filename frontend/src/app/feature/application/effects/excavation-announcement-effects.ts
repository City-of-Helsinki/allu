import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import * as fromApplication from '@feature/application/reducers';
import {Action, select, Store} from '@ngrx/store';
import {ApplicationStore} from '@service/application/application-store';
import {ExcavationAnnouncementService} from '@service/application/excavation-announcement.service';
import {Observable, of} from 'rxjs/index';
import {ExcavationAnnouncementActionType, SetRequiredTasks} from '@feature/application/actions/excavation-announcement-actions';
import {catchError, map, switchMap} from 'rxjs/internal/operators';
import {withLatestExisting} from '@feature/common/with-latest-existing';
import {NotifyFailure} from '@feature/notification/actions/notification-actions';

@Injectable()
export class ExcavationAnnouncementEffects {
  constructor(private actions: Actions,
              private store: Store<fromApplication.State>,
              private applicationStore: ApplicationStore,
              private excavationAnnouncementService: ExcavationAnnouncementService) {}

  
  setRequiredTasks: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<SetRequiredTasks>(ExcavationAnnouncementActionType.SetRequiredTasks),
    withLatestExisting(this.store.pipe(select(fromApplication.getCurrentApplication))),
    switchMap(([action, app]) => this.excavationAnnouncementService.setRequiredTasks(app.id, action.payload).pipe(
      map(updated => this.applicationStore.setAndAction(updated)),
      catchError(error => of(new NotifyFailure(error)))
    ))
  ));
}
