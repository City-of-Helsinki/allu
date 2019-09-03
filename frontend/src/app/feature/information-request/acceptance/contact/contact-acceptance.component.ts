import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {Contact} from '@model/customer/contact';
import {FormArray, FormBuilder, FormGroup} from '@angular/forms';
import {select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import * as fromInformationRequest from '@feature/information-request/reducers';
import {BehaviorSubject, combineLatest, Observable, Subject} from 'rxjs/index';
import {debounceTime, distinctUntilChanged, filter, map, switchMap, take, takeUntil, withLatestFrom} from 'rxjs/internal/operators';
import {Search} from '@feature/customerregistry/actions/contact-search-actions';
import {ArrayUtil} from '@util/array-util';
import {CONTACT_MODAL_CONFIG, ContactModalComponent} from '@feature/information-request/acceptance/contact/contact-modal.component';
import {MatDialog, MatDialogConfig} from '@angular/material/dialog';
import {NumberUtil} from '@util/number.util';
import {isEqualWithSkip} from '@util/object.util';
import {InformationRequestFieldKey} from '@model/information-request/information-request-field-key';
import {
  config as acceptanceConfig,
  ContactAcceptanceConfig
} from '@feature/information-request/acceptance/contact/contact-acceptance-config';

@Component({
  selector: 'contact-acceptance',
  templateUrl: './contact-acceptance.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ContactAcceptanceComponent implements OnInit, OnDestroy {
  @Input() formArray: FormArray;
  @Input() readonly: boolean;
  @Input() fieldKey: InformationRequestFieldKey;

  @Output() contactChanges: EventEmitter<Contact> = new EventEmitter<Contact>();

  orderer: boolean;
  referenceContact$: BehaviorSubject<Contact> = new BehaviorSubject<Contact>(undefined);
  showCreateNew$: Observable<boolean>;

  matchingContacts$: Observable<Contact[]>;
  form: FormGroup;
  searchForm: FormGroup;

  private _newContact: Contact;
  private destroy: Subject<boolean> = new Subject<boolean>();
  private search$: BehaviorSubject<string> = new BehaviorSubject('');
  private config: ContactAcceptanceConfig;

  constructor(private fb: FormBuilder,
              private store: Store<fromRoot.State>,
              protected dialog: MatDialog) {
    this.searchForm = this.fb.group({
      search: undefined
    });
  }

  @Input() set newContact(contact: Contact) {
    this._newContact = contact;
    const searchTerm = contact.name ? contact.name.toLocaleLowerCase() : '';
    this.search$.next(searchTerm);
    this.orderer = this.orderer === undefined ? contact.orderer : this.orderer;
  }

  get newContact() {
    return this._newContact;
  }

  ngOnInit(): void {
    this.form = this.fb.group({});
    this.formArray.push(this.form);

    this.config = acceptanceConfig[this.fieldKey];

    this.matchingContacts$ = this.store.pipe(
      select(this.config.getMatchingContacts),
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
    ).subscribe(search => this.store.dispatch(new Search(this.config.actionTargetType, search)));

    this.showCreateNew$ = combineLatest(
      this.referenceContact$,
      this.store.pipe(select(this.config.getCustomer))
    ).pipe(
      map(([ref, customer]) => !isEqualWithSkip(ref, this._newContact, ['id', 'customerId']))
    );

    this.search$.pipe(
      takeUntil(this.destroy)
    ).subscribe(term => this.initialSearch(term));
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  selectReferenceContact(contact?: Contact): void {
    const search = contact ? contact.name : undefined;
    this.searchForm.patchValue({search}, {emitEvent: false});
    this.referenceContact$.next(contact);
  }

  createNewContact(): void {
    this.store.pipe(
      select(this.config.getCustomer),
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

  private initialSearch(term: string): void {
    this.store.pipe(
      select(this.config.getContactsLoaded),
      filter(loaded => loaded),
      switchMap(() => this.store.pipe(select(this.config.getAvailableContacts))),
      takeUntil(this.destroy),
      map(contacts => contacts.filter(c => c.name.toLocaleLowerCase().startsWith(term))),
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
