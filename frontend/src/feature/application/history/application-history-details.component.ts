import {Component, Input, OnInit} from '@angular/core';
import {Observable} from 'rxjs/Observable';

import {ApplicationChange} from '../../../model/application/application-change/application-change';
import {User} from '../../../model/common/user';
import {MdDialogRef} from '@angular/material';

@Component({
  selector: 'application-history-details',
  template: require('./application-history-details.component.html'),
  styles: []
})
export class ApplicationHistoryDetailsComponent implements OnInit {

  @Input() change: ApplicationChange;
  @Input() user: User;

  constructor(public dialogRef: MdDialogRef<ApplicationHistoryDetailsComponent>) {}

  ngOnInit(): void {
  }
}
