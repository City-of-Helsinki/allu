import {Component, OnInit} from '@angular/core';
import {MdDialogRef} from '@angular/material';

import {User} from '../../../model/user/user';
import {UserHub} from '../../../service/user/user-hub';
import {CurrentUser} from '../../../service/user/current-user';
import {DialogCloseReason, DialogCloseValue} from '../../common/dialog-close-value';

@Component({
  selector: 'handler-modal',
  template: require('./handler-modal.component.html'),
  styles: []
})
export class HandlerModalComponent implements OnInit {
  allUsers: Array<User>;
  selectedUser: User;

  constructor(public dialogRef: MdDialogRef<HandlerModalComponent>,
              private userHub: UserHub,
              private currentUser: CurrentUser) { }

  ngOnInit(): void {
    this.currentUser.user.subscribe(u => this.selectedUser = u);
    this.userHub.getActiveUsers().subscribe(users => this.allUsers = users);
  }

  confirm() {
    this.dialogRef.close(new DialogCloseValue(DialogCloseReason.OK, this.selectedUser));
  }

  cancel() {
    this.dialogRef.close(new DialogCloseValue(DialogCloseReason.CANCEL, undefined));
  }
}
