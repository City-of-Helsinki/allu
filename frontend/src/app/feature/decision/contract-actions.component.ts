import {Component, OnDestroy, OnInit} from '@angular/core';
import * as fromRoot from '@feature/allu/reducers';
import * as fromApplication from '@feature/application/reducers';
import {Store} from '@ngrx/store';
import {Approve, CreateProposal} from '@feature/decision/actions/contract-actions';
import {MatDialog} from '@angular/material';
import {CONTRACT_APPROVAL_MODAL_CONFIG, ContractApprovalModalComponent} from '@feature/decision/contract/contract-approval-modal.component';
import {Observable, Subject} from 'rxjs/index';
import {filter, map, takeUntil} from 'rxjs/operators';
import {Application} from '@model/application/application';
import {NumberUtil} from '@util/number.util';
import {validForDecision} from '@feature/application/application-util';

@Component({
  selector: 'contract-actions',
  templateUrl: './contract-actions.component.html',
  styleUrls: ['./contract-actions.component.scss']
})
export class ContractActionsComponent implements OnInit, OnDestroy {

  isFromExternalSystem$: Observable<boolean>;
  isValidForDecision: boolean;

  private destroy: Subject<boolean> = new Subject<boolean>();

  constructor(private store: Store<fromRoot.State>,
              private dialog: MatDialog) {}

  ngOnInit(): void {
    this.isFromExternalSystem$ = this.store.select(fromApplication.isFromExternalSystem);

    this.store.select(fromApplication.getCurrentApplication).pipe(
      takeUntil(this.destroy)
    ).subscribe(app => {
      this.isValidForDecision = validForDecision(app);
    });
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  createProposal(): void {
    this.store.dispatch(new CreateProposal());
  }

  approve(): void {
    const dialogRef = this.dialog.open<ContractApprovalModalComponent>(ContractApprovalModalComponent, CONTRACT_APPROVAL_MODAL_CONFIG);
    dialogRef.afterClosed().pipe(
      filter(result => !!result)
    ).subscribe(approval => this.store.dispatch(new Approve(approval)));
  }
}
