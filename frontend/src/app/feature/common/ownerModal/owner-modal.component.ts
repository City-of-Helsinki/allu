import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';

import {User} from '../../../model/user/user';
import {CurrentUser} from '../../../service/user/current-user';
import {DialogCloseReason, DialogCloseValue} from '../dialog-close-value';

export const OWNER_MODAL_CONFIG = {width: '400px', data: {}};

@Component({
  selector: 'owner-modal',
  templateUrl: './owner-modal.component.html',
  styleUrls: []
})
export class OwnerModalComponent implements OnInit {
  allUsers: Array<User>;
  selectedUser: User;
  type: OwnerModalType;

  constructor(public dialogRef: MatDialogRef<OwnerModalComponent>,
              @Inject(MAT_DIALOG_DATA) public data: OwnerModalData,
              private currentUser: CurrentUser) { }

  ngOnInit(): void {
    this.currentUser.user.subscribe(u => this.selectedUser = u);
    this.allUsers = this.data.users || [this.selectedUser];
    this.type = this.data.type || 'OWNER';
  }

  confirm() {
    this.dialogRef.close(new DialogCloseValue(DialogCloseReason.OK, this.selectedUser));
  }

  cancel() {
    this.dialogRef.close(new DialogCloseValue(DialogCloseReason.CANCEL, undefined));
  }
}

export type OwnerModalType = 'OWNER' | 'SUPERVISOR';

export interface OwnerModalData {
  type: OwnerModalType;
  users: Array<User>;
}
