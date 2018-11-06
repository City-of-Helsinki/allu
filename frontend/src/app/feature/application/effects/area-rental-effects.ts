import {Injectable} from '@angular/core';
import {Actions, Effect, ofType} from '@ngrx/effects';
import * as fromApplication from '@feature/application/reducers';
import {Action, select, Store} from '@ngrx/store';
import {ApplicationStore} from '@service/application/application-store';
import {Observable, of} from 'rxjs/index';
import {catchError, filter, switchMap} from 'rxjs/internal/operators';
import {withLatestExisting} from '@feature/common/with-latest-existing';
import {NotifyFailure, NotifySuccess} from '@feature/notification/actions/notification-actions';
import {findTranslation} from '@util/translations';
import {ApplicationType} from '@model/application/type/application-type';
import {AreaRentalService} from '@service/application/area-rental.service';
import {DateReportActionType, ReportWorkFinished} from '@feature/application/actions/date-report-actions';

@Injectable()
export class AreaRentalEffects {
  constructor(private actions: Actions,
              private store: Store<fromApplication.State>,
              private applicationStore: ApplicationStore,
              private areaRentalService: AreaRentalService) {}

  @Effect()
  reportWorkFinished: Observable<Action> = this.actions.pipe(
    ofType<ReportWorkFinished>(DateReportActionType.ReportWorkFinished),
    withLatestExisting(this.store.pipe(select(fromApplication.getCurrentApplication))),
    filter(([action, app]) => app.type === ApplicationType.AREA_RENTAL),
    switchMap(([action, app]) => this.areaRentalService.reportWorkFinished(app.id, action.payload).pipe(
      switchMap(updated => [
        this.applicationStore.setAndAction(updated),
        new NotifySuccess(findTranslation('application.areaRental.action.reportWorkFinished'))
      ]),
      catchError(error => of(new NotifyFailure(error)))
    ))
  );
}
