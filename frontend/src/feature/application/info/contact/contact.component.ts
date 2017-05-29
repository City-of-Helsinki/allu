import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormGroup} from '@angular/forms';

import {Contact} from '../../../../model/customer/contact';
import {Some} from '../../../../util/option';
import {NumberUtil} from '../../../../util/number.util';
import {MdDialog, MdDialogRef} from '@angular/material';
import {ContactModalComponent} from '../../../customerregistry/contact/contact-modal.component';
import {Observable, Subscription} from 'rxjs';
import {CustomerHub} from '../../../../service/customer/customer-hub';
import {Customer} from '../../../../model/customer/customer';

const ALWAYS_ENABLED_FIELDS = ['id', 'name', 'customerId'];

@Component({
  selector: 'contact',
  viewProviders: [],
  template: require('./contact.component.html'),
  styles: [
    require('./contact.component.scss')
  ]
})
export class ContactComponent implements OnInit, OnDestroy {
  @Input() parentForm: FormGroup;
  @Input() customerId: number;
  @Input() customerRoleType: string;
  @Input() contactList: Array<Contact> = [];
  @Input() readonly: boolean;
  @Input() customerEvents: Observable<Customer>;

  contacts: FormArray;
  availableContacts: Observable<Array<Contact>>;
  matchingContacts: Observable<Array<Contact>>;

  private dialogRef: MdDialogRef<ContactModalComponent>;
  private customerSubscription: Subscription;

  constructor(private fb: FormBuilder,
              private dialog: MdDialog,
              private customerHub: CustomerHub) {}

  ngOnInit(): void {
    this.initContacts();

    this.contactList = Some(this.contactList).orElse([new Contact()]);
    this.contactList.forEach(contact => this.addContact(contact));

    if (this.readonly) {
      this.contacts.disable();
    }

    this.availableContacts = Some(this.customerId)
      .map(id => this.customerHub.findCustomerActiveContacts(id))
      .orElse(Observable.of([]));

    this.customerSubscription = this.customerEvents.subscribe(a => this.onCustomerChange(a));
  }

  ngOnDestroy(): void {
    this.customerSubscription.unsubscribe();
  }

  contactSelected(contact: Contact, index: number): void {
    this.contacts.at(index).patchValue(contact);
    this.disableContactEdit(index);
  }

  canBeEdited(contact: Contact): boolean {
    return NumberUtil.isDefined(contact.id) && !this.readonly;
  }

  canBeRemoved(): boolean {
    return this.contacts.length > 1 && !this.readonly;
  }

  edit(id: number, index: number): void {
    this.dialogRef = this.dialog.open(ContactModalComponent, {disableClose: false, width: '800px'});
    this.dialogRef.componentInstance.contactId = id;
    this.dialogRef.afterClosed()
      .filter(contact => !!contact)
      .subscribe(contact => this.contacts.at(index).patchValue(contact));
  }

  /**
   * Resets form values if form contained existing contact
   */
  resetContactIfExisting(index: number): void {
    let contactCtrl = this.contacts.at(index);
    if (NumberUtil.isDefined(contactCtrl.value.id)) {
      contactCtrl.reset({
        name: contactCtrl.value.name,
        active: true
      });
    }
    contactCtrl.enable();
  }

  private addContact(contact: Contact = new Contact()): void {
    let fg = Contact.formGroup(this.fb, contact);
    let nameControl = fg.get('name');
    this.matchingContacts = nameControl.valueChanges
      .debounceTime(300)
      .switchMap(name => this.onNameSearchChange(name));

    this.contacts.push(fg);

    if (NumberUtil.isDefined(contact.id)) {
      this.disableContactEdit(this.contacts.length - 1);
    }
  }

  private onNameSearchChange(term: string): Observable<Array<Contact>> {
    if (!!term) {
      return this.availableContacts
        .map(contacts => contacts.filter(c => c.nameLowercase.indexOf(term.toLowerCase()) >= 0));
    } else {
      return this.availableContacts;
    }
  }

  private remove(index: number): void {
    this.contacts.removeAt(index);
  }

  private onCustomerChange(customer: Customer) {
    this.resetContacts();
    if (NumberUtil.isDefined(customer.id)) {
      this.availableContacts = this.customerHub.findCustomerActiveContacts(customer.id);
    }
  }

  private initContacts(): void {
    this.contacts = <FormArray>this.parentForm.get('contacts');
  }

  private resetContacts(): void {
    this.contacts.reset();
    while (this.contacts.length > 1) {
      this.contacts.removeAt(1);
    }
    this.contacts.enable();
  }

  private disableContactEdit(index: number): void {
    let contactCtrl = <FormGroup>this.contacts.at(index);
    Object.keys(contactCtrl.controls)
      .filter(key => ALWAYS_ENABLED_FIELDS.indexOf(key) < 0)
      .forEach(key => contactCtrl.get(key).disable());
  }
}
