import {Component, OnInit} from '@angular/core';
import {MdDialogRef} from '@angular/material';
import {Observable} from 'rxjs/Observable';

import {User} from '../../../model/common/user';
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
  currentUserName: string;
  selectedHandler: User;

  constructor(public dialogRef: MdDialogRef<HandlerModalComponent>, private userHub: UserHub) { }

  ngOnInit(): void {
    CurrentUser.userName().do(userName => this.currentUserName = userName);
    this.userHub.getActiveUsers().subscribe(users => this.allUsers = users);
  }

  confirm() {
    this.selectedHandler = this.selectCurrentUser();
    this.dialogRef.close(new DialogCloseValue(DialogCloseReason.OK, this.selectedHandler));
  }

  cancel() {
    this.dialogRef.close(new DialogCloseValue(DialogCloseReason.CANCEL, undefined));
  }

  private selectCurrentUser(): User {
    if (this.allUsers) {
      return this.allUsers.find(user => user.userName === this.currentUserName);
    } else {
      return undefined;
    }
  }
}
