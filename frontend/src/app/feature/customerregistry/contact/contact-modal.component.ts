import {Component, Input, OnInit} from '@angular/core';
import {MatLegacyDialogRef as MatDialogRef} from '@angular/material/legacy-dialog';
import {UntypedFormBuilder, UntypedFormGroup} from '@angular/forms';
import {findTranslation} from '@util/translations';
import {NotificationService} from '@feature/notification/notification.service';
import {Contact} from '@model/customer/contact';
import {ContactService} from '@service/customer/contact.service';

@Component({
  selector: 'contact-modal',
  templateUrl: './contact-modal.component.html',
  styleUrls: [
    './contact-modal.component.scss'
  ]
})
export class ContactModalComponent implements OnInit {
  @Input() contactId: number;

  contactForm: UntypedFormGroup;

  constructor(public dialogRef: MatDialogRef<ContactModalComponent>,
              private contactService: ContactService,
              private notification: NotificationService,
              fb: UntypedFormBuilder) {
    this.contactForm = Contact.formGroup(fb);
  }

  ngOnInit(): void {
    this.contactService.findById(this.contactId)
      .subscribe(contact => this.contactForm.patchValue(contact));
  }

  onSubmit(contact: Contact) {
    this.contactService.save(contact.customerId, contact)
      .subscribe(
        saved => {
          this.notification.success(findTranslation('contact.action.save'));
          this.dialogRef.close(saved);
        }, error => this.notification.errorInfo(error));
  }

  cancel() {
    this.dialogRef.close(undefined);
  }
}
