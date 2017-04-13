import {Component, OnInit, Input} from '@angular/core';
import {MdDialogRef} from '@angular/material';
import {CustomerHub} from '../../../service/customer/customer-hub';
import {FormBuilder, FormGroup} from '@angular/forms';
import {ApplicantForm} from '../../application/info/applicant/applicant.form';
import {findTranslation} from '../../../util/translations';
import {NotificationService} from '../../../service/notification/notification.service';
import {ApplicantType} from '../../../model/application/applicant/applicant-type';
import {EnumUtil} from '../../../util/enum.util';
import {Contact} from '../../../model/application/contact';
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

  constructor(public dialogRef: MdDialogRef<ContactModalComponent>,
              private fb: FormBuilder,
              private customerHub: CustomerHub) {
    this.contactForm = Contact.formGroup(fb);
  }

  ngOnInit(): void {
    this.customerHub.findContactById(this.contactId)
      .subscribe(contact => this.contactForm.patchValue(contact));
  }

  onSubmit(contact: Contact) {
    this.customerHub.saveApplicantWithContacts(contact.applicantId, undefined, [contact])
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
