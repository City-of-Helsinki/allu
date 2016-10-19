import {Component, OnInit} from '@angular/core';
import {MdDialogRef} from '@angular/material';
import {Observable} from 'rxjs/Observable';

import {User} from '../../../model/common/user';
import {UserHub} from '../../../service/user/user-hub';

@Component({
  selector: 'handler-modal',
  template: require('./handler-modal.component.html'),
  styles: [
    require('./handler-modal.component.scss')
  ]
})
export class HandlerModalComponent implements OnInit {
  handlers: Observable<Array<User>>;
  selectedHandler: User;

  constructor(public dialogRef: MdDialogRef<HandlerModalComponent>, private userHub: UserHub) { }

  ngOnInit(): void {
    this.handlers = this.userHub.getActiveUsers();
  }

  confirm() {
    this.dialogRef.close(this.selectedHandler);
  }

  cancel() {
    this.dialogRef.close();
  }
}
