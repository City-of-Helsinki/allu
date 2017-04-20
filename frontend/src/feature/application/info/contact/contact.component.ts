import {Component, Input, OnInit, OnDestroy} from '@angular/core';
import {FormGroup, FormBuilder, Validators, FormArray} from '@angular/forms';

import {Contact} from '../../../../model/application/contact';
import {emailValidator, postalCodeValidator} from '../../../../util/complex-validator';
import {Some} from '../../../../util/option';
import {NumberUtil} from '../../../../util/number.util';
import {MdDialog, MdDialogRef} from '@angular/material';
import {ContactModalComponent} from '../../../customerregistry/contact/contact-modal.component';
import {ApplicantEvents} from '../../../customerregistry/applicant/applicant-events';
import {Observable, Subject, Subscription} from 'rxjs';
import {Applicant} from '../../../../model/application/applicant/applicant';
import {CustomerHub} from '../../../../service/customer/customer-hub';

const ALWAYS_ENABLED_FIELDS = ['id', 'name'];

@Component({
  selector: 'contact',
  viewProviders: [],
  template: require('./contact.component.html'),
  styles: [
    require('./contact.component.scss')
  ]
})
export class ContactComponent implements OnInit, OnDestroy {
  @Input() applicationForm: FormGroup;
  @Input() applicantId: number;
  @Input() contactList: Array<Contact> = [];
  @Input() readonly: boolean;
  @Input() headerText = 'Yhteyshenkilö';
  @Input() formName = 'contacts';
  @Input() addNew = false;
  @Input() saveToRegistry = false;

  contactsForm: FormGroup;
  contacts: FormArray;
  availableContacts: Observable<Array<Contact>>;
  nameSearch = new Subject<Array<Contact>>();
  nameSearchResults = this.nameSearch.asObservable();

  private dialogRef: MdDialogRef<ContactModalComponent>;
  private applicantSubscription: Subscription;

  constructor(private fb: FormBuilder,
              private dialog: MdDialog,
              private customerHub: CustomerHub,
              private applicantEvents: ApplicantEvents) {}

  ngOnInit(): void {
    this.initContacts();

    this.contactList = Some(this.contactList).orElse([new Contact()]);
    this.contactList.forEach(contact => this.addContact(contact));

    if (this.readonly) {
      this.contactsForm.disable();
    }

    this.availableContacts = Some(this.applicantId)
      .map(id => this.customerHub.findApplicantActiveContacts(id))
      .orElse(Observable.of([]));

    this.applicantSubscription = this.applicantEvents.applicantChange.subscribe(a => this.onApplicantChange(a));
  }

  ngOnDestroy(): void {
    this.applicationForm.removeControl(this.formName);
    this.applicantSubscription.unsubscribe();
  }

  onNameSearchChange(term: string = '', index: number): void {
    this.resetContactIfExisting(index);
    this.availableContacts
      .debounceTime(300)
      .map(contacts => contacts.filter(c => c.nameLowercase.indexOf(term.toLowerCase()) >= 0))
      .subscribe(contacts => this.nameSearch.next(contacts));
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

  private addContact(contact: Contact = new Contact()): void {
    this.contacts.push(Contact.formGroup(this.fb, contact));
    if (NumberUtil.isDefined(contact.id)) {
      this.disableContactEdit(this.contacts.length - 1);
    }
  }

  private remove(index: number): void {
    this.contacts.removeAt(index);
  }

  private onApplicantChange(applicant: Applicant) {
    this.resetContacts();
    if (NumberUtil.isDefined(applicant.id)) {
      this.availableContacts = this.customerHub.findApplicantActiveContacts(applicant.id);
    }
  }

  private initContacts(): void {
    this.contacts = this.fb.array([]);
    this.contactsForm = this.fb.group({
      contacts: this.contacts
    });
    this.applicationForm.addControl('contacts', this.contacts);
  }

  private resetContacts(): void {
    this.contacts.reset();
    while (this.contacts.length > 1) {
      this.contacts.removeAt(1);
    }
  }

  /**
   * Resets form values if form contained existing contact
   */
  private resetContactIfExisting(index: number): void {
    let contactCtrl = this.contacts.at(index);
    if (NumberUtil.isDefined(contactCtrl.value.id)) {
      contactCtrl.reset({
        name: contactCtrl.value.name,
        active: true
      });
      contactCtrl.enable();
    }
  }

  private disableContactEdit(index: number): void {
    let contactCtrl = <FormGroup>this.contacts.at(index);
    Object.keys(contactCtrl.controls)
      .filter(key => ALWAYS_ENABLED_FIELDS.indexOf(key) < 0)
      .forEach(key => contactCtrl.get(key).disable());
  }
}
