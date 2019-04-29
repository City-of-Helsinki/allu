import {Injectable} from '@angular/core';
import {Actions, Effect, ofType} from '@ngrx/effects';
import * as fromInformationRequest from '../reducers';
import * as InformationRequestAction from '../actions/information-request-actions';
import {InformationRequestActionType} from '../actions/information-request-actions';
import * as InformationRequestResultAction from '../actions/information-request-result-actions';
import * as ApplicationAction from '@feature/application/actions/application-actions';
import {Action, select, Store} from '@ngrx/store';
import {InformationRequestService} from '@service/application/information-request.service';
import {from, Observable, of} from 'rxjs/index';
import * as fromApplication from '@feature/application/reducers';
import {catchError, filter, map, switchMap, withLatestFrom} from 'rxjs/internal/operators';
import {NumberUtil} from '@util/number.util';
import {InformationRequestResultActionType} from '@feature/information-request/actions/information-request-result-actions';
import {ApplicationService} from '@service/application/application.service';
import {ApplicationStore} from '@service/application/application-store';
import {ApplicationStatus} from '@model/application/application-status';
import {NotifyFailure, NotifySuccess} from '@feature/notification/actions/notification-actions';
import {withLatestExisting} from '@feature/common/with-latest-existing';
import {findTranslation} from '@util/translations';

@Injectable()
export class InformationRequestEffects {
  constructor(private actions: Actions,
              private store: Store<fromInformationRequest.State>,
              private applicationStore: ApplicationStore,
              private applicationService: ApplicationService,
              private informationRequestService: InformationRequestService) {}

  @Effect()
  loadRequest: Observable<Action> = this.actions.pipe(
    ofType<InformationRequestAction.LoadLatestRequest>(InformationRequestActionType.LoadLatestRequest),
    withLatestFrom(this.store.select(fromApplication.getCurrentApplication)),
    filter(([action, application]) => NumberUtil.isExisting(application)),
    switchMap(([action, application]) => this.informationRequestService.getRequestForApplication(application.id).pipe(
      map(request => new InformationRequestAction.LoadLatestRequestSuccess(request)),
      catchError(error => from([
        new InformationRequestAction.LoadLatestRequestFailed(error),
        new NotifyFailure(error)
      ]))
    ))
  );

  @Effect()
  saveRequest: Observable<Action> = this.actions.pipe(
    ofType<InformationRequestAction.SaveRequest>(InformationRequestActionType.SaveRequest),
    switchMap(action => this.informationRequestService.save(action.payload).pipe(
      map(request => new InformationRequestAction.SaveRequestSuccess(request)),
      catchError(error => of(new NotifyFailure(error)))
    ))
  );

  @Effect()
  saveAndSendRequest: Observable<Action> = this.actions.pipe(
    ofType<InformationRequestAction.SaveAndSendRequest>(InformationRequestActionType.SaveAndSendRequest),
    switchMap(action => this.informationRequestService.save(action.payload)),
    switchMap(request => this.applicationStore.changeStatus(request.applicationId, ApplicationStatus.WAITING_INFORMATION).pipe(
      map(() => new InformationRequestAction.SaveRequestSuccess(request)),
      catchError(error => of(new NotifyFailure(error)))
    ))
  );

  @Effect()
  loadResponse: Observable<Action> = this.actions.pipe(
    ofType<InformationRequestAction.LoadLatestResponse>(InformationRequestActionType.LoadLatestResponse),
    withLatestFrom(this.store.select(fromApplication.getCurrentApplication)),
    filter(([action, application]) => NumberUtil.isExisting(application)),
    switchMap(([action, application]) => this.informationRequestService.getForApplication(application.id).pipe(
      map(response => new InformationRequestAction.LoadLatestResponseSuccess(response)),
      catchError(error => from([
        new InformationRequestAction.LoadLatestResponseFailed(error),
        new NotifyFailure(error)
      ]))
    ))
  );

  @Effect()
  saveResult: Observable<Action> = this.actions.pipe(
    ofType<InformationRequestResultAction.Save>(InformationRequestResultActionType.Save),
    switchMap(action => this.applicationStore.saveInformationRequestResult(action.payload).pipe(
      map(() => new InformationRequestResultAction.SaveSuccess(action.payload)),
      catchError(error => of(new NotifyFailure(error)))
    ))
  );

  @Effect()
  closeRequest: Observable<Action> = this.actions.pipe(
    ofType<InformationRequestResultAction.SaveSuccess>(InformationRequestResultActionType.SaveSuccess),
    filter(action => NumberUtil.isDefined(action.payload.informationRequestId)),
    switchMap(action => this.informationRequestService.closeInformationRequest(action.payload.informationRequestId).pipe(
      switchMap((closed) => [
        new InformationRequestAction.LoadLatestRequestSuccess(closed),
        new NotifySuccess(findTranslation('informationRequest.action.responseHandled')),
        new InformationRequestAction.LoadLatestResponseSuccess(undefined),
        new ApplicationAction.Load(action.payload.application.id)
      ]),
      catchError(error => of(new NotifyFailure(error)))
    ))
  );

  @Effect()
  cancelRequest: Observable<Action> = this.actions.pipe(
    ofType<InformationRequestAction.CancelRequest>(InformationRequestActionType.CancelRequest),
    switchMap(action => this.informationRequestService.delete(action.paylod)),
    withLatestExisting(this.store.pipe(select(fromApplication.getCurrentApplication))),
    switchMap(([_, app]) => this.applicationStore.changeStatus(app.id, ApplicationStatus.HANDLING).pipe(
      map(() => new InformationRequestAction.CancelRequestSuccess()),
      catchError(error => of(new NotifyFailure(error)))
    ))
  );

  @Effect()
  clearApplicationClientData: Observable<Action> = this.actions.pipe(
    ofType<InformationRequestResultAction.SaveSuccess>(InformationRequestResultActionType.SaveSuccess),
    filter(action => !!action.payload.application.clientApplicationData),
    map(() => new ApplicationAction.RemoveClientApplicationData())
  );
}
