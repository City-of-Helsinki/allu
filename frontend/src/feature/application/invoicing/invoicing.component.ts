import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {ApplicationState} from '../../../service/application/application-state';
import {InvoicingInfoForm} from './invoicing-info/invoicing-info.form';
import {ChargeBasisEntryForm} from './charge-basis-entry.form';
import {InvoiceHub} from '../../../service/application/invoice/invoice-hub';
import {ChargeBasisEntry} from '../../../model/application/invoice/charge-basis-entry';
import {Observable} from 'rxjs/Observable';
import {NotificationService} from '../../../service/notification/notification.service';
import {findTranslation} from '../../../util/translations';
import {Application} from '../../../model/application/application';

@Component({
  selector: 'invoicing',
  template: require('./invoicing.component.html'),
  styles: []
})
export class InvoicingComponent implements OnInit {

  invoicingForm: FormGroup;
  applicationId: number;

  constructor(private fb: FormBuilder, private applicationState: ApplicationState, private invoiceHub: InvoiceHub) {
    this.invoicingForm = this.fb.group({});
  }

  ngOnInit(): void {
    this.applicationId = this.applicationState.application.id;
  }

  onSubmit(form: FormGroup): void {
    let value: InvoicingForm = form.value;
    this.saveApplicationInfo(value)
      .switchMap(app => this.saveInvoiceRows(value))
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

  private saveApplicationInfo(form: InvoicingForm): Observable<Application> {
    let application = this.applicationState.application;
    const invoicingInfo = form.invoicingInfo;
    application.invoiceRecipientId = invoicingInfo.invoicingAddress.id;
    application.notBillable = invoicingInfo.notBillable;
    application.notBillableReason = invoicingInfo.notBillableReason;
    return this.applicationState.save(application);
  }

  private saveInvoiceRows(form: InvoicingForm): Observable<Array<ChargeBasisEntry>> {
    let chargeBasisEntries = this.toChargeBasisEntries(form.pendingInvoiceRows, form.acceptedInvoiceRows);
    return this.invoiceHub.saveInvoiceRows(this.applicationId, chargeBasisEntries);
  }

  private toChargeBasisEntries(pending: Array<ChargeBasisEntryForm>, accepted: Array<ChargeBasisEntryForm>): Array<ChargeBasisEntry> {
    let pendingRows = pending.map(row => ChargeBasisEntryForm.toChargeBasisEntry(row));
    let acceptedRows = accepted.map(row => ChargeBasisEntryForm.toChargeBasisEntry(row));
    return pendingRows.concat(acceptedRows);
  }
}

interface InvoicingForm {
  invoicingInfo: InvoicingInfoForm;
  pendingInvoiceRows: Array<ChargeBasisEntryForm>;
  acceptedInvoiceRows: Array<ChargeBasisEntryForm>;
}
