import {Component, Input, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormGroup} from '@angular/forms';

import {Contact} from '../../../../model/customer/contact';
import {Some} from '../../../../util/option';
import {NumberUtil} from '../../../../util/number.util';
import {MatDialog, MatDialogRef} from '@angular/material';
import {ContactModalComponent} from '../../../customerregistry/contact/contact-modal.component';
import {Observable} from 'rxjs';
import {CustomerHub} from '../../../../service/customer/customer-hub';
import {CustomerWithContactsForm} from '../../../customerregistry/customer/customer-with-contacts.form';
import {CustomerRoleType} from '../../../../model/customer/customer-role-type';
import {ApplicationState} from '../../../../service/application/application-state';
import {ApplicationType} from '../../../../model/application/type/application-type';
import {OrdererIdForm} from '../cable-report/cable-report.form';

const ALWAYS_ENABLED_FIELDS = ['id', 'name', 'customerId', 'orderer'];

@Component({
  selector: 'contact',
  viewProviders: [],
  template: require('./contact.component.html'),
  styles: [
    require('./contact.component.scss')
  ]
})
export class ContactComponent implements OnInit {
  @Input() parentForm: FormGroup;
  @Input() customerId: number;
  @Input() customerRoleType: string;
  @Input() contactList: Array<Contact> = [];
  @Input() readonly: boolean;
  @Input() contactRequired = false;

  form: FormGroup;
  contacts: FormArray;
  availableContacts: Observable<Array<Contact>>;
  matchingContacts: Observable<Array<Contact>>;
  showOrderer: boolean = false;

  private dialogRef: MatDialogRef<ContactModalComponent>;

  constructor(private fb: FormBuilder,
              private dialog: MatDialog,
              private customerHub: CustomerHub,
              private applicationState: ApplicationState) {}

  ngOnInit(): void {
    this.initContacts();
    this.showOrderer = ApplicationType.CABLE_REPORT === this.applicationState.application.typeEnum;

    if (this.readonly) {
      this.contacts.disable();
    }

    this.availableContacts = Some(this.customerId)
      .map(id => this.customerHub.findCustomerActiveContacts(id))
      .orElse(Observable.of([]));
  }

  contactSelected(contact: Contact, index: number): void {
    this.contacts.at(index).patchValue(contact);
    this.disableContactEdit(index);
  }

  canBeEdited(contact: Contact): boolean {
    return NumberUtil.isDefined(contact.id) && !this.readonly;
  }

  canBeRemoved(): boolean {
    const contactCanBeRemoved = !this.contactRequired || (this.contacts.length > 1);
    const canBeEdited = !this.readonly;
    return contactCanBeRemoved && canBeEdited;
  }

  selectOrderer(index: number): void {
    const contact = this.contacts.at(index).value;
    this.parentForm.patchValue({ordererId: new OrdererIdForm(contact.id, this.customerRoleType, index)});
  }

  isOrderer(index: number): boolean {
    const contact = this.contacts.at(index).value;
    return Some(this.parentForm.getRawValue().ordererId)
      .map(form => OrdererIdForm.to(form))
      .map(currentOrderer => currentOrderer.matches(contact.id, this.customerRoleType, index))
      .orElse(false);
  }

  edit(id: number, index: number): void {
    this.dialogRef = this.dialog.open<ContactModalComponent>(ContactModalComponent, {
      disableClose: false,
      width: '800px'
    });
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

  onCustomerChange(customerId: number) {
    this.resetContacts();
    if (NumberUtil.isDefined(customerId)) {
      this.availableContacts = this.customerHub.findCustomerActiveContacts(customerId);
    }
  }

  addContact(contact: Contact = new Contact()): void {
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

  /**
   * If customer is removed for some reason from form
   * and current customer contained contact which was orderer
   * then reset orderer
   */
  onCustomerRemove() {
    Some(this.parentForm.value.ordererId)
      .map(form => OrdererIdForm.to(form))
      .filter(ordererId => this.contacts.value.some(contact => ordererId.idOrRoleTypeMatches(contact.id, this.customerRoleType)))
      .do(ordererId => this.parentForm.patchValue({ordererId: OrdererIdForm.createDefault()}));
  }

  remove(index: number): void {
    if (this.isOrderer(index)) {
      this.parentForm.patchValue({ordererId: OrdererIdForm.createDefault()});
    }

    this.contacts.removeAt(index);
  }

  private onNameSearchChange(term: string): Observable<Array<Contact>> {
    if (!!term) {
      return this.availableContacts
        .map(contacts => contacts.filter(c => c.nameLowercase.indexOf(term.toLowerCase()) >= 0));
    } else {
      return this.availableContacts;
    }
  }

  private initContacts(): void {
    this.form = <FormGroup>this.parentForm.get(CustomerWithContactsForm.formName(CustomerRoleType[this.customerRoleType]));
    this.contacts = <FormArray>this.form.get('contacts');
    const defaultContactList = this.contactRequired ? [new Contact()] : [];
    this.contactList = Some(this.contactList).filter(cl => cl.length > 0).orElse(defaultContactList);
    this.contactList.forEach(contact => this.addContact(contact));
  }

  private resetContacts(): void {
    this.contacts.reset();
    while (this.contacts.length > 1) {
      this.remove(1);
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
