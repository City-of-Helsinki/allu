import {Component, OnInit} from '@angular/core';
import {ApplicationState} from '../../../service/application/application-state';

@Component({
  selector: 'invoicing',
  templateUrl: './invoicing.component.html',
  styleUrls: []
})
export class InvoicingComponent implements OnInit {

  applicationId: number;

  constructor(private applicationState: ApplicationState) {
  }

  ngOnInit(): void {
    this.applicationId = this.applicationState.application.id;
  }
}
