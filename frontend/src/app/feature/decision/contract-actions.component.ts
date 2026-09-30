import {Component, OnDestroy, OnInit} from '@angular/core';
import * as fromRoot from '@feature/allu/reducers';
import * as fromApplication from '@feature/application/reducers';
import {Store} from '@ngrx/store';
import {Approve, CreateProposal, Reject} from '@feature/decision/actions/contract-actions';
import {MatDialog} from '@angular/material/dialog';
import {CONTRACT_APPROVAL_MODAL_CONFIG, ContractApprovalModalComponent} from '@feature/decision/contract/contract-approval-modal.component';
import {Observable, Subject} from 'rxjs';
import {filter, map, switchMap, take, takeUntil} from 'rxjs/operators';
import {Application} from '@model/application/application';
import {findTranslation} from '@util/translations';
import {ApplicationStatus} from '@model/application/application-status';
import {BaseDecisionActionsComponent} from '@feature/decision/base-decision-actions.component';

@Component({
  selector: 'contract-actions',
  templateUrl: './contract-actions.component.html',
  styleUrls: ['./contract-actions.component.scss']
})
export class ContractActionsComponent extends BaseDecisionActionsComponent implements OnInit, OnDestroy {
  isFromExternalSystem$: Observable<boolean>;
  isWaitingForContract: boolean;

  private destroy: Subject<boolean> = new Subject<boolean>();

  constructor(
    private myStore: Store<fromRoot.State>,
    private dialog: MatDialog
  ) {
    super(myStore);
  }

  ngOnInit(): void {
    this.watchDecisionBlocked();
    this.isFromExternalSystem$ = this.store.select(fromApplication.isFromExternalSystem);

    this.store.select(fromApplication.getCurrentApplication).pipe(
      takeUntil(this.destroy)
    ).subscribe(app => {
      this.isWaitingForContract = ApplicationStatus.WAITING_CONTRACT_APPROVAL === app.status;
    });
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  createProposal(): void {
    this.store.dispatch(new CreateProposal());
  }

  rejectProposal(): void {
    this.store.dispatch(new Reject(findTranslation('contract.reject.defaultReason')));
  }

  approve(): void {
    this.store.select(fromApplication.getCurrentApplication).pipe(
      take(1),
      map(app => this.createConfig(app)),
      map(config => this.dialog.open<ContractApprovalModalComponent>(ContractApprovalModalComponent, config)),
      switchMap(modalRef => modalRef.afterClosed()),
      filter(result => !!result)
    ).subscribe(approval => this.store.dispatch(new Approve(approval)));
  }

  private createConfig(app: Application) {
    return {
      ...CONTRACT_APPROVAL_MODAL_CONFIG,
      data: {
        applicationType: app.type
      }
    };
  }
}
