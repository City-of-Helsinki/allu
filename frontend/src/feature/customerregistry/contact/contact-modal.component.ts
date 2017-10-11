import {Component, Input, OnInit} from '@angular/core';
import {MatDialogRef} from '@angular/material';
import {CustomerHub} from '../../../service/customer/customer-hub';
import {FormBuilder, FormGroup} from '@angular/forms';
import {findTranslation} from '../../../util/translations';
import {NotificationService} from '../../../service/notification/notification.service';
import {Contact} from '../../../model/customer/contact';
import {ArrayUtil} from '../../../util/array-util';

@Component({
  selector: 'contact-modal',
  template: require('./contact-modal.component.html'),
  styles: [
    require('./contact-modal.component.scss')
  ]
})
export class ContactModalComponent implements OnInit {
  @Input() contactId: number;

  contactForm: FormGroup;

  constructor(public dialogRef: MatDialogRef<ContactModalComponent>,
              private customerHub: CustomerHub,
              fb: FormBuilder) {
    this.contactForm = Contact.formGroup(fb);
  }

  ngOnInit(): void {
    this.customerHub.findContactById(this.contactId)
      .subscribe(contact => this.contactForm.patchValue(contact));
  }

  onSubmit(contact: Contact) {
    this.customerHub.saveContactsForCustomer(contact.customerId, [contact])
      .subscribe(
        saved => {
          NotificationService.message(findTranslation('contact.action.save'));
          this.dialogRef.close(ArrayUtil.first(saved.contacts));
        }, error => NotificationService.error(error));
  }

  cancel() {
    this.dialogRef.close(undefined);
  }
}
