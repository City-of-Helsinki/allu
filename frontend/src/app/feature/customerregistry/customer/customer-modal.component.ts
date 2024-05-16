import {Component, Input, OnInit} from '@angular/core';
import {MatLegacyDialogRef as MatDialogRef} from '@angular/material/legacy-dialog';
import {UntypedFormBuilder, UntypedFormGroup} from '@angular/forms';
import {CustomerForm} from './customer.form';
import {findTranslation} from '../../../util/translations';
import {NotificationService} from '../../notification/notification.service';
import {CustomerService} from '../../../service/customer/customer.service';

export const CUSTOMER_MODAL_CONFIG = {width: '800PX', disableClose: false, data: {}};

@Component({
  selector: 'customer-modal',
  templateUrl: './customer-modal.component.html',
  styleUrls: [
    './customer-modal.component.scss'
  ]
})
export class CustomerModalComponent implements OnInit {
  @Input() customerId: number;

  customerForm: UntypedFormGroup;

  constructor(public dialogRef: MatDialogRef<CustomerModalComponent>,
              private customerService: CustomerService,
              private notification: NotificationService,
              fb: UntypedFormBuilder) {
    this.customerForm = CustomerForm.initialForm(fb);
  }

  ngOnInit(): void {
    this.customerService.findCustomerById(this.customerId)
      .subscribe(customer => this.customerForm.patchValue(CustomerForm.fromCustomer(customer)));
  }

  onSubmit(customerForm: CustomerForm) {
    const customer = CustomerForm.toCustomer(customerForm);
    this.customerService.saveCustomer(customer)
      .subscribe(
        saved => {
          this.notification.success(findTranslation('customer.action.save'));
          this.dialogRef.close(saved);
        }, error => this.notification.errorInfo(error));
  }

  cancel() {
    this.dialogRef.close(undefined);
  }
}
