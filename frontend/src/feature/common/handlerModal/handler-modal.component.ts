import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';

import {User} from '../../../model/user/user';
import {CurrentUser} from '../../../service/user/current-user';
import {DialogCloseReason, DialogCloseValue} from '../dialog-close-value';

export const HANDLER_MODAL_CONFIG = {width: '400px', data: {}};

@Component({
  selector: 'handler-modal',
  template: require('./handler-modal.component.html'),
  styles: []
})
export class HandlerModalComponent implements OnInit {
  allUsers: Array<User>;
  selectedUser: User;
  type: HandlerModalType;

  constructor(public dialogRef: MatDialogRef<HandlerModalComponent>,
              @Inject(MAT_DIALOG_DATA) public data: HandlerModalData,
              private currentUser: CurrentUser) { }

  ngOnInit(): void {
    this.currentUser.user.subscribe(u => this.selectedUser = u);
    this.allUsers = this.data.users || [this.selectedUser];
    this.type = this.data.type || 'HANDLER';
  }

  confirm() {
    this.dialogRef.close(new DialogCloseValue(DialogCloseReason.OK, this.selectedUser));
  }

  cancel() {
    this.dialogRef.close(new DialogCloseValue(DialogCloseReason.CANCEL, undefined));
  }
}

export type HandlerModalType = 'HANDLER' | 'SUPERVISOR';

export interface HandlerModalData {
  type: HandlerModalType;
  users: Array<User>;
};
