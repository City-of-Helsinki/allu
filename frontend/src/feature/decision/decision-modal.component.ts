import {Component, Input, OnInit} from '@angular/core';
import {MdDialogRef} from '@angular/material';
import {FormBuilder, FormGroup} from '@angular/forms';

import {ApplicationStatusChange} from '../../model/application/application-status-change';
import {ApplicationStatus} from '../../model/application/application-status';

export const DECISION_MODAL_CONFIG = {width: '800px'};

@Component({
  selector: 'decision-modal',
  template: require('./decision-modal.component.html'),
  styles: [require('./decision-modal.component.scss')]
})
export class DecisionModalComponent implements OnInit {
  @Input() status: string;
  @Input() applicationId: number;

  decisionForm: FormGroup;

  constructor(public dialogRef: MdDialogRef<DecisionModalComponent>, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.decisionForm = this.fb.group({
      comment: ['']
    });
  }

  confirm() {
    let statusChange = new ApplicationStatusChange(
      this.applicationId,
      ApplicationStatus[this.status],
      this.decisionForm.value.comment);

    this.dialogRef.close(statusChange);
  }

  cancel() {
    this.dialogRef.close();
  }
}
