import * as fromContract from '@feature/decision/reducers/contract-reducer';
import * as fromApplication from '@feature/application/reducers';
import {Injectable} from '@angular/core';
import {Action, Store} from '@ngrx/store';
import {Actions, Effect, ofType} from '@ngrx/effects';
import {Observable, of} from 'rxjs/index';
import {ContractActionType, Load, LoadFailed, LoadSuccess} from '@feature/decision/actions/contract-actions';
import {catchError, filter, map, switchMap, withLatestFrom} from 'rxjs/internal/operators';
import {NumberUtil} from '@util/number.util';
import {ContractService} from '@service/contract/contract.service';
import {Application} from '@model/application/application';
import {ApplicationStatus} from '@model/application/application-status';

@Injectable()
export class ContractEffects {
  constructor(private actions: Actions,
              private store: Store<fromContract.State>,
              private contractService: ContractService) {
  }

  @Effect()
  loadContract: Observable<Action> = this.actions.pipe(
    ofType<Load>(ContractActionType.Load),
    withLatestFrom(this.store.select(fromApplication.getCurrentApplication)),
    filter(([action, application]) => NumberUtil.isExisting(application)),
    switchMap(([action, application]) => this.loadAvailableContract(application))
  );

  private loadAvailableContract(application: Application): Observable<Action> {
    const contract = application.statusEnum < ApplicationStatus.WAITING_CONTRACT_APPROVAL
      ? this.contractService.fetchPreview(application.id)
      : this.contractService.fetch(application.id);

    return contract.pipe(
      map(response => new LoadSuccess(response)),
      catchError(error => of(new LoadFailed(error)))
    );
  }
}
