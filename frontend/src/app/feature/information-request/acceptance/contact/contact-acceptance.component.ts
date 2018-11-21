import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {Contact} from '@model/customer/contact';
import {FormArray, FormBuilder, FormGroup} from '@angular/forms';
import {select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import * as fromCustomerSearch from '@feature/customerregistry/reducers';
import * as fromInformationRequest from '@feature/information-request/reducers';
import {BehaviorSubject, combineLatest, Observable, Subject} from 'rxjs/index';
import {debounceTime, distinctUntilChanged, filter, map, switchMap, take, takeUntil, withLatestFrom} from 'rxjs/internal/operators';
import {Search} from '@feature/customerregistry/actions/contact-search-actions';
import {ArrayUtil} from '@util/array-util';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {CONTACT_MODAL_CONFIG, ContactModalComponent} from '@feature/information-request/acceptance/contact/contact-modal.component';
import {MatDialog, MatDialogConfig} from '@angular/material';
import {NumberUtil} from '@util/number.util';
import {isEqualWithSkip} from '@util/object.util';

@Component({
  selector: 'contact-acceptance',
  templateUrl: './contact-acceptance.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ContactAcceptanceComponent implements OnInit, OnDestroy {
  @Input() formArray: FormArray;
  @Input() readonly: boolean;

  @Output() contactChanges: EventEmitter<Contact> = new EventEmitter<Contact>();

  referenceContact$: BehaviorSubject<Contact> = new BehaviorSubject<Contact>(undefined);
  showCreateNew$: Observable<boolean>;

  matchingContacts$: Observable<Contact[]>;
  form: FormGroup;
  searchForm: FormGroup;

  private _newContact: Contact;
  private destroy: Subject<boolean> = new Subject<boolean>();

  constructor(private fb: FormBuilder,
              private store: Store<fromRoot.State>,
              protected dialog: MatDialog) {
    this.searchForm = this.fb.group({
      search: undefined
    });
  }

  @Input() set newContact(contact: Contact) {
    this._newContact = contact;
    this.initialSearch();
  }

  get newContact() {
    return this._newContact;
  }

  ngOnInit(): void {
    this.form = this.fb.group({});
    this.formArray.push(this.form);

    this.matchingContacts$ = this.store.pipe(
      select(fromCustomerSearch.getMatchingApplicantContacts),
      withLatestFrom(this.store.pipe(
        select(fromInformationRequest.getResultContacts),
        map(selected => selected.map(contact => contact.id))
      )),
      // Need to filter by selected so user does not pick up duplicates
      map(([matching, selected]) => matching.filter(contact => !selected.some(c => c === contact.id))),
      distinctUntilChanged()
    );

    this.searchForm.get('search').valueChanges.pipe(
      takeUntil(this.destroy),
      debounceTime(300)
    ).subscribe(search => this.store.dispatch(new Search(ActionTargetType.Applicant, search)));

    this.showCreateNew$ = combineLatest(
      this.referenceContact$,
      this.store.pipe(select(fromInformationRequest.getResultCustomer))
    ).pipe(
      map(([ref, customer]) => !isEqualWithSkip(ref, this._newContact, ['id', 'customerId']))
    );
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  selectReferenceContact(contact?: Contact): void {
    const searchContact = contact || this.newContact;
    const search = searchContact ? searchContact.name : undefined;
    this.searchForm.patchValue({search}, {emitEvent: false});
    this.referenceContact$.next(contact);
  }

  createNewContact(): void {
    this.store.pipe(
      select(fromInformationRequest.getResultCustomer),
      filter(customer => NumberUtil.isExisting(customer)),
      take(1),
      map(customer => this.createModalConfig(customer.id)),
      switchMap(config => this.dialog.open(ContactModalComponent, config).afterClosed()),
      filter(contact => !!contact)
    ).subscribe(contact => {
      this.selectReferenceContact(contact);
      this._newContact = contact;
    });
  }

  private initialSearch(): void {
    const searchTerm = this.newContact.name ? this.newContact.name.toLocaleLowerCase() : '';
    this.store.pipe(
      select(fromCustomerSearch.getApplicantContactsLoaded),
      filter(loaded => loaded),
      switchMap(() => this.store.pipe(select(fromCustomerSearch.getAvailableApplicantContacts))),
      take(1),
      map(contacts => contacts.filter(c => c.name.toLocaleLowerCase().startsWith(searchTerm))),
      map(contacts => ArrayUtil.first(contacts)),
    ).subscribe(matching => this.selectReferenceContact(matching));
  }

  private createModalConfig(customerId: number): MatDialogConfig {
    return {
      ...CONTACT_MODAL_CONFIG,
      data: {
        customerId,
        contact: this._newContact
      }
    };
  }
}
