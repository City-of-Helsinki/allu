import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {NotificationService} from '@feature/notification/notification.service';
import {Contact} from '@model/customer/contact';
import {CustomerService} from '@service/customer/customer.service';
import {findTranslation} from '@util/translations';
import {ContactService} from '@service/customer/contact.service';

export const CONTACT_MODAL_CONFIG = {width: '600px', data: {}};

export interface ContactModalData {
  customerId: number;
  contact: Contact;
}

@Component({
  selector: 'contact-modal',
  templateUrl: './contact-modal.component.html',
  styleUrls: ['../info-acceptance/person-modal.component.scss']
})
export class ContactModalComponent implements OnInit {
  contact: Contact;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: ContactModalData,
    private dialogRef: MatDialogRef<ContactModalComponent>,
    private customerService: CustomerService,
    private contactService: ContactService,
    private notification: NotificationService) {}

  ngOnInit(): void {
    this.contact = this.data.contact;
  }

  confirm() {
    this.contactService.save(this.data.customerId, this.contact)
      .subscribe(
        saved => {
          this.notification.success(findTranslation('contact.action.save'));
          this.dialogRef.close(saved);
        }, error => this.notification.errorInfo(error));
  }

  cancel() {
    this.dialogRef.close();
  }
}
