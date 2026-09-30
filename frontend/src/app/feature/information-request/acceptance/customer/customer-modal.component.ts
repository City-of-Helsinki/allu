import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {findTranslation} from '@util/translations';
import {CustomerService} from '@service/customer/customer.service';
import {NotificationService} from '@feature/notification/notification.service';
import {Customer} from '@model/customer/customer';

export const CUSTOMER_MODAL_CONFIG = {width: '600px', data: {}};

export interface CustomerModalData {
  customer: Customer;
}

@Component({
  selector: 'customer-modal',
  templateUrl: './customer-modal.component.html',
  styleUrls: ['../info-acceptance/person-modal.component.scss']
})
export class CustomerModalComponent implements OnInit {
  customer: Customer;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: CustomerModalData,
    private dialogRef: MatDialogRef<CustomerModalComponent>,
    private customerService: CustomerService,
    private notification: NotificationService) {}

  ngOnInit(): void {
    this.customer = this.data.customer;
  }

  confirm() {
    this.customerService.saveCustomer(this.customer)
      .subscribe(
        saved => {
          this.notification.success(findTranslation('customer.action.save'));
          this.dialogRef.close(saved);
        }, error => this.notification.errorInfo(error));
  }

  cancel() {
    this.dialogRef.close();
  }
}
