import {Component, Input, OnInit} from '@angular/core';
import {MdDialogRef} from '@angular/material';
import {CustomerHub} from '../../../service/customer/customer-hub';
import {FormBuilder, FormGroup} from '@angular/forms';
import {CustomerForm} from './customer.form';
import {findTranslation} from '../../../util/translations';
import {NotificationService} from '../../../service/notification/notification.service';

@Component({
  selector: 'customer-modal',
  template: require('./customer-modal.component.html'),
  styles: [
    require('./customer-modal.component.scss')
  ]
})
export class CustomerModalComponent implements OnInit {
  @Input() customerId: number;

  customerForm: FormGroup;

  constructor(public dialogRef: MdDialogRef<CustomerModalComponent>,
              private customerHub: CustomerHub,
              fb: FormBuilder) {
    this.customerForm = CustomerForm.initialForm(fb);
  }

  ngOnInit(): void {
    this.customerHub.findCustomerById(this.customerId)
      .subscribe(customer => this.customerForm.patchValue(CustomerForm.fromCustomer(customer)));
  }

  onSubmit(customerForm: CustomerForm) {
    let customer = CustomerForm.toCustomer(customerForm);
    this.customerHub.saveCustomerWithContacts(customer.id, customer, [])
      .subscribe(
        saved => {
          NotificationService.message(findTranslation('customer.action.save'));
          this.dialogRef.close(saved.customer);
        }, error => NotificationService.error(error));
  }

  cancel() {
    this.dialogRef.close(undefined);
  }
}
