import {Component, Input, OnInit} from '@angular/core';
import {MatDialogRef} from '@angular/material';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {CommentType} from '../../../model/application/comment/comment-type';
import {StatusChangeComment} from '../../../model/application/status-change-comment';

export const DECISION_PROPOSAL_MODAL_CONFIG = {width: '800px'};

@Component({
  selector: 'decision-proposal-modal',
  template: require('./decision-proposal-modal.component.html'),
  styles: [require('./decision-proposal-modal.component.scss')]
})
export class DecisionProposalModalComponent implements OnInit {
  @Input() proposal: string;

  proposalForm: FormGroup;

  constructor(public dialogRef: MatDialogRef<DecisionProposalModalComponent>,
              private fb: FormBuilder) {}

  ngOnInit(): void {
    this.proposalForm = this.fb.group({
      comment: ['', Validators.required]
    });
  }

  confirm() {
    this.dialogRef.close(new StatusChangeComment(CommentType[this.proposal], this.proposalForm.value.comment));
  }

  cancel() {
    this.dialogRef.close();
  }
}
