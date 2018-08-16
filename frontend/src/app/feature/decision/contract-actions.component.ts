import {Component, OnInit} from '@angular/core';
import * as fromRoot from '@feature/allu/reducers';
import * as fromApplication from '@feature/application/reducers';
import {Store} from '@ngrx/store';
import {Approve, CreateProposal} from '@feature/decision/actions/contract-actions';
import {MatDialog} from '@angular/material';
import {CONTRACT_APPROVAL_MODAL_CONFIG, ContractApprovalModalComponent} from '@feature/decision/contract/contract-approval-modal.component';
import {Observable} from 'rxjs/index';

@Component({
  selector: 'contract-actions',
  templateUrl: './contract-actions.component.html',
  styleUrls: ['./contract-actions.component.scss']
})
export class ContractActionsComponent implements OnInit {

  isFromExternalSystem$: Observable<boolean>;

  constructor(private store: Store<fromRoot.State>,
              private dialog: MatDialog) {}

  ngOnInit(): void {
    this.isFromExternalSystem$ = this.store.select(fromApplication.isFromExternalSystem);
  }

  createProposal(): void {
    this.store.dispatch(new CreateProposal());
  }

  approve(): void {
    const dialogRef = this.dialog.open<ContractApprovalModalComponent>(ContractApprovalModalComponent, CONTRACT_APPROVAL_MODAL_CONFIG);
    dialogRef.afterClosed()
      .subscribe(approval => this.store.dispatch(new Approve(approval)));
  }
}
