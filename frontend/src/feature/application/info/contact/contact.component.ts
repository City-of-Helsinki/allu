import {Component, Input, OnInit, OnDestroy} from '@angular/core';
import {FormGroup, FormBuilder, Validators, FormArray} from '@angular/forms';

import {Contact} from '../../../../model/application/contact';
import {emailValidator, postalCodeValidator} from '../../../../util/complex-validator';
import {Some} from '../../../../util/option';
import {NumberUtil} from '../../../../util/number.util';
import {MdDialog, MdDialogRef} from '@angular/material';
import {ContactModalComponent} from '../../../customerregistry/contact/contact-modal.component';

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

  private dialogRef: MdDialogRef<ContactModalComponent>;

  constructor(private fb: FormBuilder, private dialog: MdDialog) {}

  ngOnInit(): void {
    this.contacts = this.fb.array([]);
    this.contactsForm = this.fb.group({
      contacts: this.contacts
    });
    this.applicationForm.addControl(this.formName, this.contacts);

    this.contactList = Some(this.contactList).orElse([new Contact()]);
    this.contactList.forEach(contact => this.addContact(contact));

    if (this.readonly) {
      this.contactsForm.disable();
    }
  }

  ngOnDestroy(): void {
    this.applicationForm.removeControl(this.formName);
  }

  canBeEdited(contact: Contact): boolean {
    return NumberUtil.isDefined(contact.id) && !this.readonly;
  }

  edit(id: number, index: number): void {
    this.dialogRef = this.dialog.open(ContactModalComponent, {disableClose: false, width: '800px'});
    this.dialogRef.componentInstance.contactId = id;
    this.dialogRef.afterClosed()
      .filter(contact => !!contact)
      .subscribe(contact => this.contacts.at(index).patchValue(contact));
  }

  private addContact(contact: Contact = new Contact()): void {
    this.contacts.push(Contact.formGroup(this.fb, contact));
  }

  private removeContact(index: number): void {
    this.contacts.removeAt(index);
  }
}
