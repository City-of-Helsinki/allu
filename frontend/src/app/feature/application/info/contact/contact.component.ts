import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {UntypedFormArray, UntypedFormBuilder, UntypedFormGroup} from '@angular/forms';
import {Contact} from '@model/customer/contact';
import {Some} from '@util/option';
import {NumberUtil} from '@util/number.util';
import {Observable, of, Subject} from 'rxjs';
import {CustomerWithContactsForm} from '@feature/customerregistry/customer/customer-with-contacts.form';
import {CustomerRoleType} from '@model/customer/customer-role-type';
import {ApplicationStore} from '@service/application/application-store';
import {ApplicationType} from '@model/application/type/application-type';
import {fromOrdererId, toOrdererId} from '../cable-report/cable-report.form';
import {FormUtil} from '@util/form.util';
import {CustomerService} from '@service/customer/customer.service';
import {debounceTime, filter, map, switchMap, take, takeUntil, tap} from 'rxjs/internal/operators';
import {DistributionEntry} from '@model/common/distribution-entry';
import {DistributionType} from '@model/common/distribution-type';
import {OrdererId} from '@model/application/cable-report/orderer-id';
import {BehaviorSubject} from 'rxjs/internal/BehaviorSubject';
import {findTranslation} from '@util/translations';
import {NotificationService} from '@feature/notification/notification.service';
import {ContactService} from '@service/customer/contact.service';
import {Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {AddToDistribution} from '@feature/application/actions/application-actions';

const ALWAYS_ENABLED_FIELDS = ['id', 'name', 'customerId', 'orderer'];

@Component({
  selector: 'contact',
  viewProviders: [],
  templateUrl: './contact.component.html',
  styleUrls: [
    './contact.component.scss'
  ]
})
export class ContactComponent implements OnInit, OnDestroy {
  @Input() parentForm: UntypedFormGroup;
  @Input() customerRoleType: string;
  @Input() readonly: boolean;
  @Input() contactRequired = false;

  @Output() contactSelectChange: EventEmitter<Contact> = new EventEmitter<Contact>();

  form: UntypedFormGroup;
  contacts: UntypedFormArray;
  availableContacts: Observable<Array<Contact>>;
  matchingContacts: Observable<Array<Contact>>;
  showOrderer = false;

  private customerIdChanges = new BehaviorSubject<number>(undefined);
  private destroy: Subject<boolean> = new Subject<boolean>();

  private matchingContactsMap: Map<number, Observable<Array<Contact>>> = new Map();

  constructor(private fb: UntypedFormBuilder,
              private customerService: CustomerService,
              private contactService: ContactService,
              private applicationStore: ApplicationStore,
              private notification: NotificationService,
              private store: Store<fromRoot.State>) {}

  ngOnInit(): void {
    this.availableContacts = this.customerIdChanges.pipe(
      takeUntil(this.destroy),
      filter(id => NumberUtil.isDefined(id)),
      switchMap(id => this.customerService.findCustomerActiveContacts(id))
    );

    this.initContacts();
    this.showOrderer = ApplicationType.CABLE_REPORT === this.applicationStore.snapshot.application.type;

    if (this.readonly) {
      this.contacts.disable();
    }
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  contactSelected(contact: Contact, index: number): void {
    this.contacts.at(index).patchValue(contact);
    this.disableContactEdit(index);
    this.contactSelectChange.emit(contact);
  }

  canBeRemoved(): boolean {
    const contactCanBeRemoved = !this.contactRequired || (this.contacts.length > 1);
    const canBeEdited = !this.readonly;
    return contactCanBeRemoved && canBeEdited;
  }

  selectOrderer(index: number): void {
    const contact = this.contacts.at(index).value;
    const ordererId = OrdererId.of(contact.id, this.customerRoleType, index);
    this.parentForm.patchValue({ordererId: fromOrdererId(ordererId)});
  }

  isOrderer(index: number): boolean {
    if (this.contacts.controls.length <= index) {
      return false;
    }
    const contact = this.contacts.at(index).value;
    return Some(this.parentForm.getRawValue().ordererId)
      .map(form => toOrdererId(form))
      .map(currentOrderer => currentOrderer.matches(contact.id, this.customerRoleType, index))
      .orElse(false);
  }

  /**
   * Resets form values if form contained existing contact
   */
  resetContactIfExisting(index: number): void {
    const contactCtrl = this.contacts.at(index);
    if (NumberUtil.isDefined(contactCtrl.value.id)) {
      contactCtrl.reset({
        name: contactCtrl.value.name,
        active: true
      });
      this.resetOrdererIfMatchingIndex(index);
    }
    contactCtrl.enable();
  }

  onKeyup(event: KeyboardEvent, index: number): void {
    if (event.code !== 'Enter') {
      this.resetContactIfExisting(index);
    }
  }

  onCustomerChange(customerId: number) {
    this.customerIdChanges.next(customerId);
  }

  addContact(contact: Contact = new Contact()): void {
    const fg = Contact.formGroup(this.fb, contact);
    const nameControl = fg.get('name');

    const matchingContacts$ = nameControl.valueChanges.pipe(
      debounceTime(300),
      switchMap(name => this.onNameSearchChange(name))
    );

    this.matchingContactsMap.set(this.contacts.length,  matchingContacts$);

    this.contacts.push(fg);

    if (NumberUtil.isDefined(contact.id)) {
      this.disableContactEdit(this.contacts.length - 1);
    }
  }

  showSaveContact$(contact: Contact): Observable<boolean> {
    const newContact = !NumberUtil.isExisting(contact);
    return this.customerIdChanges.pipe(
      take(1),
      map(id => NumberUtil.isDefined(id) && newContact)
    );
  }

  save(contact: Contact, index: number): void {
    this.customerIdChanges.pipe(
      filter(id => NumberUtil.isDefined(id)),
      take(1),
      switchMap(customerId => this.contactService.save(customerId, contact))
    ).subscribe(saved => {
      this.notification.success(findTranslation('contact.action.save'));
      this.contactSelected(saved, index);
    });
  }

  /**
   * If customer is removed for some reason from form
   * and current customer contained contact which was orderer
   * then reset orderer
   */
  onCustomerRemove() {
    this.contacts.controls.forEach((contact, index) => this.resetOrdererIfMatchingIndex(index));
  }

  remove(index: number): void {
    this.resetOrdererIfMatchingIndex(index);
    this.contacts.removeAt(index);

    const updatedMap = new Map<number, Observable<Array<Contact>>>();

    Array.from(this.matchingContactsMap.entries()).forEach(([key, value]) => {
      if (key < index) {
        updatedMap.set(key, value);
      } else if (key > index) {
        updatedMap.set(key - 1, value);
      }
    });

    this.matchingContactsMap = updatedMap;
  }

  canBeAddedToDistribution(index: number): boolean {
    const contact = <UntypedFormGroup>this.contacts.at(index);
    const email = contact.getRawValue().email;
    return !this.readonly && !!email && email.length > 2;
  }

  addToDistribution(index: number): void {
    const contactFg = <UntypedFormGroup>this.contacts.at(index);
    const contact = contactFg.getRawValue();
    const distributionEntry = new DistributionEntry(null, contact.name, DistributionType.EMAIL, contact.email);
    this.store.dispatch(new AddToDistribution(distributionEntry));
  }

  resetContacts(): void {
    while (this.contacts.length > 1) {
      this.remove(1);
    }
    this.resetOrdererIfMatchingIndex(0);
    this.contacts.reset();
    this.contacts.enable();
  }

  getMatchingContacts(index: number): Observable<Array<Contact>> {
    return this.matchingContactsMap.get(index) || of([]);
  }

  private onNameSearchChange(term: string): Observable<Array<Contact>> {
    if (!!term) {
      if (NumberUtil.isDefined(this.customerIdChanges.value)) {
        return this.searchForCurrentCustomer(term);
      } else {
        return this.searchForAnyCustomer(term);
      }
    } else {
      return this.availableContacts;
    }
  }

  private searchForCurrentCustomer(term: string): Observable<Contact[]> {
    return this.availableContacts.pipe(
      map(contacts => contacts.filter(c => c.name.toLowerCase().indexOf(term.toLowerCase()) >= 0))
    );
  }

  private searchForAnyCustomer(term: string): Observable<Contact[]> {
    return this.contactService.search({name: term, active: true});
  }

  private initContacts(): void {
    this.form = <UntypedFormGroup>this.parentForm.get(CustomerWithContactsForm.formName(CustomerRoleType[this.customerRoleType]));
    this.contacts = <UntypedFormArray>this.form.get('contacts');
    const defaultContactList = this.contactRequired ? [new Contact()] : [];
    const roleType = CustomerRoleType[this.customerRoleType];
    this.applicationStore.application.pipe(
      map(app => app.customerWithContactsByRole(roleType)),
      tap(cwc => {
        this.customerIdChanges.next(cwc.customerId)
      }),
      map(cwc => cwc.contacts.length > 0 ? cwc.contacts : defaultContactList),
      takeUntil(this.destroy)
    ).subscribe(contacts => {

      FormUtil.clearArray(this.contacts);
      contacts.forEach(contact => {
        this.addContact(contact)
      });
    });
  }

  private disableContactEdit(index: number): void {
    const contactCtrl = <UntypedFormGroup>this.contacts.at(index);
    Object.keys(contactCtrl.controls)
      .filter(key => ALWAYS_ENABLED_FIELDS.indexOf(key) < 0)
      .forEach(key => contactCtrl.get(key).disable());
  }

  private resetOrdererIfMatchingIndex(index: number) {
    
    if (this.isOrderer(index)) {
      this.parentForm.patchValue({ordererId: undefined});
    }
  }
}
