import {Component, OnInit, Input, Output, EventEmitter} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {ApplicationState} from '../../../service/application/application-state';
import {InvoicingInfoForm} from './invoicing-info/invoicing-info.form';
import {InvoiceRowForm} from './invoice-row.form';

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

  onSubmit(form: InvoicingForm): void {
    console.log('Would save form', form);
  }

  cancel(): void {
    console.log('Would reset form');
  }
}

interface InvoicingForm {
  invoicingInfo: InvoicingInfoForm;
  pendingInvoiceRows: Array<InvoiceRowForm>;
  acceptedInvoiceRows: Array<InvoiceRowForm>;
}
