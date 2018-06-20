import {Injectable} from '@angular/core';
import {Actions, Effect, ofType} from '@ngrx/effects';
import * as fromInformationRequest from '../reducers/information-request-reducer';
import * as InformationRequestAction from '../actions/information-request-actions';
import {Action, Store} from '@ngrx/store';
import {InformationRequestService} from '../../../service/application/information-request.service';
import {Observable, of} from 'rxjs/index';
import {InformationRequestActionType} from '../actions/information-request-actions';
import * as fromApplication from '../../application/reducers';
import {catchError, filter, map, switchMap, withLatestFrom} from 'rxjs/internal/operators';
import {NumberUtil} from '../../../util/number.util';

@Injectable()
export class InformationRequestEffects {
  constructor(private actions: Actions,
              private store: Store<fromInformationRequest.State>,
              private informationRequestService: InformationRequestService) {}

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
}
