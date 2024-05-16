import * as fromDecision from '@feature/decision/reducers';
import * as fromApplication from '@feature/application/reducers';
import {Injectable} from '@angular/core';
import {Action, Store} from '@ngrx/store';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {from, Observable, of} from 'rxjs/index';
import {
  Approve,
  ApproveFailed,
  ApproveSuccess,
  ContractActionType,
  CreateProposal,
  CreateProposalFailed,
  CreateProposalSuccess,
  Load,
  LoadFailed,
  LoadSuccess, Reject, RejectSuccess
} from '@feature/decision/actions/contract-actions';
import * as ApplicationAction from '@feature/application/actions/application-actions';
import {catchError, filter, map, switchMap, withLatestFrom} from 'rxjs/internal/operators';
import {NumberUtil} from '@util/number.util';
import {ContractService} from '@service/contract/contract.service';
import {Application} from '@model/application/application';
import {ApplicationStatus, isBefore} from '@model/application/application-status';
import {DocumentActionType, SetTab} from '@feature/decision/actions/document-actions';
import {DecisionTab} from '@feature/decision/documents/decision-tab';
import {NotifyFailure, NotifySuccess} from '@feature/notification/actions/notification-actions';
import {findTranslation} from '@util/translations';

@Injectable()
export class ContractEffects {
  constructor(private actions: Actions,
              private store: Store<fromDecision.DecisionState>,
              private contractService: ContractService) {
  }

  
  loadContract: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<Load>(ContractActionType.Load),
    withLatestFrom(this.store.select(fromApplication.getCurrentApplication)),
    filter(([action, application]) => NumberUtil.isExisting(application)),
    switchMap(([action, application]) => this.loadAvailableContract(application))
  ));

  
  contractTabOpen: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<SetTab>(DocumentActionType.SetTab),
    filter(action => action.payload === DecisionTab.CONTRACT),
    withLatestFrom(this.store.select(fromDecision.getContract)),
    map(([action, contract]) => {
      if (contract) {
        return new LoadSuccess(contract);
      } else {
        return new Load();
      }
    })
  ));

  
  createProposal: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<CreateProposal>(ContractActionType.CreateProposal),
    withLatestFrom(this.store.select(fromApplication.getCurrentApplication)),
    filter(([action, application]) => NumberUtil.isExisting(application)),
    switchMap(([action, application]) => this.contractService.createProposal(application.id).pipe(
      map(contract => new CreateProposalSuccess(contract)),
      catchError(error => from([
        new CreateProposalFailed(error),
        new NotifyFailure(error)
      ]))
    ))
  ));

  
  approve: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<Approve>(ContractActionType.Approve),
    withLatestFrom(this.store.select(fromApplication.getCurrentApplication)),
    filter(([action, application]) => NumberUtil.isExisting(application)),
    switchMap(([action, application]) => this.contractService.approve(application.id, action.payload).pipe(
      map(contract => new ApproveSuccess(contract)),
      catchError(error => from([
        new ApproveFailed(error),
        new NotifyFailure(error)
      ]))
    ))
  ));

  
  reject: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<Reject>(ContractActionType.Reject),
    withLatestFrom(this.store.select(fromApplication.getCurrentApplication)),
    filter(([action, application]) => NumberUtil.isExisting(application)),
    switchMap(([action, application]) => this.contractService.reject(application.id, action.payload).pipe(
      switchMap(() => [
        new RejectSuccess(),
        new NotifySuccess(findTranslation('contract.action.rejected'))
      ]),
      catchError(error => from([
        new NotifyFailure(error)
      ]))
    ))
  ));

  
  reloadApplication: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType(ContractActionType.ApproveSuccess, ContractActionType.CreateProposalSuccess, ContractActionType.RejectSuccess),
    withLatestFrom(this.store.select(fromApplication.getCurrentApplication)),
    map(([contract, application]) => new ApplicationAction.Load(application.id))
  ));

  private loadAvailableContract(application: Application): Observable<Action> {
    const contract = isBefore(application.status, ApplicationStatus.WAITING_CONTRACT_APPROVAL)
      ? this.contractService.fetchPreview(application.id)
      : this.contractService.fetch(application.id);

    return contract.pipe(
      map(response => new LoadSuccess(response)),
      catchError(error => of(new LoadFailed(error)))
    );
  }
}
