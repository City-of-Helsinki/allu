import {Component, OnInit, Input, Output, EventEmitter} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {ApplicationState} from '../../../service/application/application-state';

@Component({
  selector: 'invoicing',
  template: require('./invoicing.component.html'),
  styles: []
})
export class InvoicingComponent implements OnInit {

  invoicingForm: FormGroup;
  applicationId: number;

  constructor(private fb: FormBuilder, private applicationState: ApplicationState) {
    this.invoicingForm = fb.group({});
  }

  ngOnInit(): void {
    this.applicationId = this.applicationState.application.id;
  }
}
