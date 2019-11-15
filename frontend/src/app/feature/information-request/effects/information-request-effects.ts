import {Injectable} from '@angular/core';
import {Actions, Effect, ofType} from '@ngrx/effects';
import * as fromInformationRequest from '../reducers';
import * as InformationRequestAction from '../actions/information-request-actions';
import {InformationRequestActionType} from '../actions/information-request-actions';
import * as InformationRequestResultAction from '../actions/information-request-result-actions';
import * as ApplicationAction from '@feature/application/actions/application-actions';
import * as SummaryAction from '@feature/information-request/actions/information-request-summary-actions';
import * as ResponseAction from '@feature/information-request/actions/information-request-response-actions';
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
import {InformationRequestStatus} from '@model/information-request/information-request-status';
import {InformationRequestSummaryActionType} from '@feature/information-request/actions/information-request-summary-actions';
import {
  InformationRequestResponseActionType
} from '@feature/information-request/actions/information-request-response-actions';

@Injectable()
export class InformationRequestEffects {
  constructor(private actions: Actions,
              private store: Store<fromInformationRequest.State>,
              private applicationStore: ApplicationStore,
              private applicationService: ApplicationService,
              private informationRequestService: InformationRequestService) {}

  @Effect()
  loadRequest: Observable<Action> = this.actions.pipe(
    ofType<InformationRequestAction.LoadRequest>(InformationRequestActionType.LoadRequest),
    switchMap(action => this.informationRequestService.getRequest(action.payload).pipe(
      map(request => new InformationRequestAction.LoadRequestSuccess(request)),
      catchError(error => from([
        new InformationRequestAction.LoadRequestFailed(error),
        new NotifyFailure(error)
      ]))
    ))
  );

  @Effect()
  loadLatestRequest: Observable<Action> = this.actions.pipe(
    ofType<InformationRequestAction.LoadActiveRequest>(InformationRequestActionType.LoadActiveRequest),
    withLatestFrom(this.store.select(fromApplication.getCurrentApplication)),
    filter(([action, application]) => NumberUtil.isExisting(application)),
    switchMap(([action, application]) => this.informationRequestService.getRequestForApplication(application.id).pipe(
      map(request => new InformationRequestAction.LoadRequestSuccess(request)),
      catchError(error => from([
        new InformationRequestAction.LoadRequestFailed(error),
        new NotifyFailure(error)
      ]))
    ))
  );

  @Effect()
  saveRequest: Observable<Action> = this.actions.pipe(
    ofType<InformationRequestAction.SaveRequest>(InformationRequestActionType.SaveRequest),
    switchMap(action => this.informationRequestService.save(action.payload).pipe(
      switchMap(request => [
        new InformationRequestAction.SaveRequestSuccess(request),
        new SummaryAction.Load()
      ]),
      catchError(error => of(new NotifyFailure(error)))
    ))
  );

  @Effect()
  saveAndSendRequest: Observable<Action> = this.actions.pipe(
    ofType<InformationRequestAction.SaveAndSendRequest>(InformationRequestActionType.SaveAndSendRequest),
    switchMap(action => this.informationRequestService.save(action.payload)),
    switchMap(request => this.applicationStore.changeStatus(request.applicationId, ApplicationStatus.WAITING_INFORMATION).pipe(
      switchMap(() => [
        new InformationRequestAction.SaveRequestSuccess(request),
        new SummaryAction.Load()
      ]),
      catchError(error => of(new NotifyFailure(error)))
    ))
  );

  @Effect()
  loadResponse: Observable<Action> = this.actions.pipe(
    ofType<ResponseAction.LoadResponse>(InformationRequestResponseActionType.LoadResponse),
    filter((action) => NumberUtil.isDefined(action.payload)),
    switchMap((action) => this.informationRequestService.getResponseForRequest(action.payload).pipe(
      map(response => new ResponseAction.LoadResponseSuccess(response)),
      catchError(error => from([
        new ResponseAction.LoadResponseFailed(error),
        new NotifyFailure(error)
      ]))
    ))
  );

  @Effect()
  saveResult: Observable<Action> = this.actions.pipe(
    ofType<InformationRequestResultAction.Save>(InformationRequestResultActionType.Save),
    switchMap(action => this.applicationStore.saveInformationRequestResult(action.payload).pipe(
      switchMap(() => [
        new InformationRequestResultAction.SaveSuccess(action.payload),
        new InformationRequestAction.CloseRequest(action.payload.informationRequestId)
      ]),
      catchError(error => of(new NotifyFailure(error)))
    ))
  );

  @Effect()
  closeRequest: Observable<Action> = this.actions.pipe(
    ofType<InformationRequestAction.CloseRequest>(InformationRequestActionType.CloseRequest),
    filter(action => NumberUtil.isDefined(action.payload)),
    switchMap(action => this.informationRequestService.closeInformationRequest(action.payload).pipe(
      switchMap((closed) => [
        new InformationRequestAction.LoadRequestSuccess(closed),
        new NotifySuccess(findTranslation('informationRequest.action.responseHandled')),
        new ApplicationAction.Load(closed.applicationId),
        new SummaryAction.Load()
      ]),
      catchError(error => of(new NotifyFailure(error)))
    ))
  );

  @Effect()
  cancelRequest: Observable<Action> = this.actions.pipe(
    ofType<InformationRequestAction.CancelRequest>(InformationRequestActionType.CancelRequest),
    switchMap(action => this.informationRequestService.delete(action.payload).pipe(
      withLatestExisting(this.store.pipe(select(fromApplication.getCurrentApplication))),
      switchMap(([_, app]) => this.applicationStore.changeStatus(app.id, ApplicationStatus.HANDLING)),
      switchMap(() => [
        new InformationRequestAction.CancelRequestSuccess(action.payload),
        new SummaryAction.Load()
      ]),
      catchError(error => of(new NotifyFailure(error)))
    ))
  );

  @Effect()
  clearApplicationClientData: Observable<Action> = this.actions.pipe(
    ofType<InformationRequestResultAction.SaveSuccess>(InformationRequestResultActionType.SaveSuccess),
    filter(action => !!action.payload.application.clientApplicationData),
    map(() => new ApplicationAction.RemoveClientApplicationData())
  );

  @Effect()
  onLoadRequestSuccess: Observable<Action> = this.actions.pipe(
    ofType<InformationRequestAction.LoadRequestSuccess>(InformationRequestActionType.LoadRequestSuccess),
    filter(action => action.payload && action.payload.status === InformationRequestStatus.RESPONSE_RECEIVED),
    map(action => new ResponseAction.LoadResponse(action.payload.informationRequestId))
  );

  @Effect()
  getSummaries: Observable<Action> = this.actions.pipe(
    ofType<SummaryAction.Get>(InformationRequestSummaryActionType.Get),
    withLatestFrom(this.store.pipe(select(fromInformationRequest.getSummariesLoaded))),
    filter(([action, loaded]) => !loaded),
    map(([action, loaded]) => new SummaryAction.Load())
  );

  @Effect()
  loadSummaries: Observable<Action> = this.actions.pipe(
    ofType<SummaryAction.Load>(InformationRequestSummaryActionType.Load),
    withLatestExisting(this.store.pipe(select(fromApplication.getCurrentApplication))),
    switchMap(([action, app]) => this.informationRequestService.getSummariesForApplication(app.id).pipe(
      map(summaries => new SummaryAction.LoadSuccess(summaries)),
      catchError(error => of(new NotifyFailure(error)))
    ))
  );
}
