import {Component, Input, OnInit} from '@angular/core';
import {MatDialogRef} from '@angular/material';
import {FormBuilder, FormGroup} from '@angular/forms';

import {ApplicationStatusChange} from '../../model/application/application-status-change';
import {ApplicationStatus} from '../../model/application/application-status';
import {DistributionEntry} from '../../model/common/distribution-entry';
import {DecisionConfirmation} from '../../model/decision/decision-confirmation';
import {DecisionDetails} from '../../model/decision/decision-details';
import {DistributionEntryForm} from '../application/distribution/distribution-list/distribution-entry-form';
import {StatusChangeComment} from '../../model/application/status-change-comment';

export const DECISION_MODAL_CONFIG = {width: '800px'};

@Component({
  selector: 'decision-modal',
  template: require('./decision-modal.component.html'),
  styles: [require('./decision-modal.component.scss')]
})
export class DecisionModalComponent implements OnInit {
  @Input() status: string;
  @Input() applicationId: number;
  @Input() distributionList: Array<DistributionEntry> = [];

  decisionForm: FormGroup;

  constructor(public dialogRef: MatDialogRef<DecisionModalComponent>,
              private fb: FormBuilder) {}

  ngOnInit(): void {
    this.decisionForm = this.fb.group({
      comment: [''],
      emailMessage: ['']
    });
  }

  confirm() {
    let applicationStatus = ApplicationStatus[this.status];
    let statusChange = new ApplicationStatusChange(
      this.applicationId,
      applicationStatus,
      StatusChangeComment.fromStatus(applicationStatus, this.decisionForm.value.comment)
    );

    let decisionDetails = new DecisionDetails(
      this.decisionDistribution(),
      this.decisionForm.value.emailMessage);

    this.dialogRef.close(new DecisionConfirmation(statusChange, decisionDetails));
  }

  cancel() {
    this.dialogRef.close();
  }

  private decisionDistribution(): Array<DistributionEntry> {
    return this.decisionForm.value.distributionRows
      .map(d => DistributionEntryForm.to(d));
  }
}
