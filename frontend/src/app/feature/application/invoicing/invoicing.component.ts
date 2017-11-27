import {Component, OnInit} from '@angular/core';
import {ApplicationState} from '../../../service/application/application-state';
import {InvoicingInfoForm} from './invoicing-info/invoicing-info.form';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {NotificationService} from '../../../service/notification/notification.service';
import {Observable} from 'rxjs/Observable';
import {Application} from '../../../model/application/application';
import {Customer} from '../../../model/customer/customer';
import {CustomerForm} from '../../customerregistry/customer/customer.form';
import {CustomerHub} from '../../../service/customer/customer-hub';

@Component({
  selector: 'invoicing',
  templateUrl: './invoicing.component.html',
  styleUrls: []
})
export class InvoicingComponent implements OnInit {

  applicationId: number;
  infoForm: FormGroup;

  private recipientForm: FormGroup;
  private notBillableCtrl: FormControl;

  constructor(private applicationState: ApplicationState,
              private fb: FormBuilder,
              private customerHub: CustomerHub) {
  }

  ngOnInit(): void {
    this.applicationId = this.applicationState.application.id;
    this.infoForm = InvoicingInfoForm.initialForm(this.fb);
    this.recipientForm = <FormGroup>this.infoForm.get('invoiceRecipient');
    this.notBillableCtrl = <FormControl>this.infoForm.get('notBillable');
  }

  onSubmit(): void {
    this.saveApplicationInfo()
      .subscribe(
        rows => NotificationService.translateMessage('invoice.action.save'),
        error => NotificationService.errorMessage(error));
  }

  cancel(): void {
    this.infoForm.reset();
  }

  private saveApplicationInfo(): Observable<Application> {
    const application = this.applicationState.application;

    const invoicingInfo: InvoicingInfoForm = this.infoForm.getRawValue();
    application.notBillable = invoicingInfo.notBillable;
    application.notBillableReason = invoicingInfo.notBillable ? invoicingInfo.notBillableReason : undefined;


    return this.saveCustomer()
      .switchMap(customer => {
        application.invoiceRecipientId = customer.id;
        return this.applicationState.save(application);
      });
  }

  private saveCustomer(): Observable<Customer> {
    const billable = !this.notBillableCtrl.value;
    const customer = CustomerForm.toCustomer(this.recipientForm.getRawValue());
    if (billable && this.recipientForm.dirty) {
      return this.customerHub.saveCustomer(customer);
    } else {
      return Observable.of(customer);
    }
  }
}
