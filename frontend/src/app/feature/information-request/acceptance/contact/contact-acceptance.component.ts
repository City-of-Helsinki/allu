import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {Contact} from '@model/customer/contact';
import {FormArray, FormBuilder, FormGroup} from '@angular/forms';
import {Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import * as fromCustomerSearch from '@feature/customerregistry/reducers';
import * as fromInformationRequest from '@feature/information-request/reducers';
import {BehaviorSubject, Observable, Subject} from 'rxjs/index';
import {debounceTime, distinctUntilChanged, filter, map, switchMap, takeUntil, withLatestFrom} from 'rxjs/internal/operators';
import {Search} from '@feature/customerregistry/actions/contact-search-actions';
import {ArrayUtil} from '@util/array-util';

@Component({
  selector: 'contact-acceptance',
  templateUrl: './contact-acceptance.component.html'
})
export class ContactAcceptanceComponent implements OnInit, OnDestroy {
  @Input() formArray: FormArray;
  @Input() readonly: boolean;

  @Output() contactChanges: EventEmitter<Contact> = new EventEmitter<Contact>();

  referenceContact$: BehaviorSubject<Contact> = new BehaviorSubject<Contact>(undefined);

  matchingContacts$: Observable<Contact[]>;
  form: FormGroup;
  searchForm: FormGroup;

  private _newContact: Contact;
  private destroy: Subject<boolean> = new Subject<boolean>();

  constructor(private fb: FormBuilder,
              private store: Store<fromRoot.State>) {}


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
    this.searchForm = this.fb.group({
      search: undefined
    });

    this.matchingContacts$ = this.store.select(fromCustomerSearch.getMatchingContacts).pipe(
      withLatestFrom(this.store.select(fromInformationRequest.getResultContacts).pipe(
        map(selected => selected.map(contact => contact.id))
      )),
      // Need to filter by selected so user does not pick up duplicates
      map(([matching, selected]) => matching.filter(contact => !selected.some(c => c === contact.id))),
      distinctUntilChanged()
    );

    this.searchForm.get('search').valueChanges.pipe(
      takeUntil(this.destroy),
      debounceTime(300)
    ).subscribe(search => this.store.dispatch(new Search(search)));
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  selectReferenceContact(contact?: Contact): void {
    this.referenceContact$.next(contact);
  }

  createNewContact(): void {
    this.searchForm.reset();
    this.selectReferenceContact(new Contact());
  }

  private initialSearch(): void {
    const searchTerm = this.newContact.name ? this.newContact.name.toLocaleLowerCase() : '';
    this.store.select(fromCustomerSearch.getContactsLoaded).pipe(
      filter(loaded => loaded),
      switchMap(() => this.store.select(fromCustomerSearch.getAvailableContacts)),
      map(contacts => contacts.filter(c => c.name.toLocaleLowerCase().startsWith(searchTerm))),
      map(contacts => ArrayUtil.first(contacts)),
    ).subscribe(matching => {
      if (matching) {
        this.selectReferenceContact(matching);
        this.searchForm.patchValue({search: matching.name}, {emitEvent: false});
      } else {
        this.selectReferenceContact(new Contact());
      }
    });
  }
}
