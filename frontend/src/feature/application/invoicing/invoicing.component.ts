import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {ApplicationState} from '../../../service/application/application-state';
import {InvoicingInfoForm} from './invoicing-info/invoicing-info.form';
import {InvoiceRowForm} from './invoice-row.form';
import {InvoiceHub} from '../../../service/application/invoice/invoice-hub';
import {InvoiceRow} from '../../../model/application/invoice/invoice-row';
import {Observable} from 'rxjs/Observable';
import {NotificationService} from '../../../service/notification/notification.service';
import {findTranslation} from '../../../util/translations';

@Component({
  selector: 'invoicing',
  template: require('./invoicing.component.html'),
  styles: []
})
export class InvoicingComponent implements OnInit {

  invoicingForm: FormGroup;
  applicationId: number;

  constructor(private fb: FormBuilder, private applicationState: ApplicationState, private invoiceHub: InvoiceHub) {
    this.invoicingForm = fb.group({});
  }

  ngOnInit(): void {
    this.applicationId = this.applicationState.application.id;
  }

  onSubmit(form: FormGroup): void {
    let value: InvoicingForm = form.value;
    // TODO: Save other invoice information
    this.saveInvoiceRows(value)
      .subscribe(
        rows => NotificationService.message(findTranslation('invoice.action.save')),
        error => NotificationService.errorMessage(error));
  }

  cancel(): void {
    this.invoicingForm.reset();
    this.invoiceHub.loadInvoiceRows(this.applicationId)
      .subscribe(
        rows => NotificationService.message(findTranslation('invoice.action.cancel')),
        error => NotificationService.error(error));
  }

  private saveInvoiceRows(form: InvoicingForm): Observable<Array<InvoiceRow>> {
    let invoiceRows = this.toInvoiceRows(form.pendingInvoiceRows, form.acceptedInvoiceRows);
    return this.invoiceHub.saveInvoiceRows(this.applicationId, invoiceRows);
  }

  private toInvoiceRows(pending: Array<InvoiceRowForm>, accepted: Array<InvoiceRowForm>): Array<InvoiceRow> {
    let pendingRows = pending.map(row => InvoiceRowForm.toInvoiceRow(row));
    let acceptedRows = accepted.map(row => InvoiceRowForm.toInvoiceRow(row));
    return pendingRows.concat(acceptedRows);
  }
}

interface InvoicingForm {
  invoicingInfo: InvoicingInfoForm;
  pendingInvoiceRows: Array<InvoiceRowForm>;
  acceptedInvoiceRows: Array<InvoiceRowForm>;
}
