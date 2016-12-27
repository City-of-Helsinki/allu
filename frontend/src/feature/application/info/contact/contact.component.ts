import {Component, Input, OnInit, OnDestroy} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormGroup, FormBuilder, Validators, FormArray} from '@angular/forms';

import {ApplicationHub} from '../../../../service/application/application-hub';
import {Contact} from '../../../../model/application/contact';
import {translations} from '../../../../util/translations';
import {emailValidator} from '../../../../util/complex-validator';
import {Some} from '../../../../util/option';

@Component({
  selector: 'contact',
  viewProviders: [],
  template: require('./contact.component.html'),
  styles: []
})
export class ContactComponent implements OnInit, OnDestroy {
  @Input() applicationForm: FormGroup;
  @Input() contactList: Array<Contact> = [];
  @Input() readonly: boolean;
  @Input() headerText = 'Yhteyshenkilö';
  @Input() formName = 'contacts';
  @Input() addNew = false;
  @Input() saveToRegistry = false;

  contactsForm: FormGroup;
  contacts: FormArray;
  translations = translations;

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.contacts = this.fb.array([]);
    this.contactsForm = this.fb.group({
      contacts: this.contacts
    });
    this.applicationForm.addControl(this.formName, this.contacts);

    this.contactList = Some(this.contactList).orElse([new Contact()]);
    this.contactList.forEach(contact => this.addContact(contact));
  }

  ngOnDestroy(): void {
    this.applicationForm.removeControl(this.formName);
  }

  private addContact(contact: Contact): void {
    this.contacts.push(this.createContact(contact));
  }

  private removeContact(index: number): void {
    this.contacts.removeAt(index);
  }

  private createContact(contact: Contact): FormGroup {
    return this.fb.group({
      id: contact.id,
      applicantId: contact.applicantId,
      name: [contact.name, [Validators.required, Validators.minLength(2)]],
      streetAddress: [contact.streetAddress],
      postalCode: [contact.postalCode],
      city: [contact.city],
      email: [contact.email, emailValidator],
      phone: [contact.phone, Validators.minLength(2)]
    });
  }
}
