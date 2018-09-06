import {Injectable} from '@angular/core';
import {Actions, Effect, ofType} from '@ngrx/effects';
import * as fromInformationRequest from '../reducers';
import * as InformationRequestAction from '../actions/information-request-actions';
import * as InformationRequestResultAction from '../actions/information-request-result-actions';
import * as ApplicationAction from '@feature/application/actions/application-actions';
import {Action, Store} from '@ngrx/store';
import {InformationRequestService} from '../../../service/application/information-request.service';
import {Observable, of} from 'rxjs/index';
import {InformationRequestActionType} from '../actions/information-request-actions';
import * as fromApplication from '../../application/reducers';
import {catchError, filter, map, switchMap, withLatestFrom} from 'rxjs/internal/operators';
import {NumberUtil} from '../../../util/number.util';
import {InformationRequestResultActionType} from '@feature/information-request/actions/information-request-result-actions';
import {ApplicationService} from '@service/application/application.service';
import {ApplicationStore} from '@service/application/application-store';

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
      catchError(error => of(new InformationRequestAction.LoadLatestRequestFailed(error)))
    ))
  );

  @Effect()
  saveRequest: Observable<Action> = this.actions.pipe(
    ofType<InformationRequestAction.SaveRequest>(InformationRequestActionType.SaveRequest),
    switchMap(action => this.informationRequestService.save(action.payload).pipe(
      map(request => new InformationRequestAction.SaveRequestSuccess(request)),
      catchError(error => of(new InformationRequestAction.SaveRequestFailed(error)))
    ))
  )

  @Effect()
  loadResponse: Observable<Action> = this.actions.pipe(
    ofType<InformationRequestAction.LoadLatestResponse>(InformationRequestActionType.LoadLatestResponse),
    withLatestFrom(this.store.select(fromApplication.getCurrentApplication)),
    filter(([action, application]) => NumberUtil.isExisting(application)),
    switchMap(([action, application]) => this.informationRequestService.getForApplication(application.id).pipe(
      map(response => new InformationRequestAction.LoadLatestResponseSuccess(response)),
      catchError(error => of(new InformationRequestAction.LoadLatestResponseFailed(error)))
    ))
  );

  @Effect()
  saveResult: Observable<Action> = this.actions.pipe(
    ofType<InformationRequestResultAction.Save>(InformationRequestResultActionType.Save),
    switchMap(action => this.applicationStore.saveInformationRequestResult(action.payload).pipe(
      map(() => new InformationRequestResultAction.SaveSuccess(action.payload)),
      catchError(error => of(new InformationRequestResultAction.SaveFailed(error)))
    ))
  );

  @Effect()
  closeRequest: Observable<Action> = this.actions.pipe(
    ofType<InformationRequestResultAction.SaveSuccess>(InformationRequestResultActionType.SaveSuccess),
    filter(action => NumberUtil.isDefined(action.payload.informationRequestId)),
    switchMap(action => this.informationRequestService.closeInformationRequest(action.payload.informationRequestId).pipe(
      switchMap(() => [
        new InformationRequestAction.LoadLatestResponseSuccess(undefined),
        new ApplicationAction.Load(action.payload.application.id)
      ]),
      catchError(error => of(new InformationRequestResultAction.CloseFailed(error)))
    ))
  );
}
