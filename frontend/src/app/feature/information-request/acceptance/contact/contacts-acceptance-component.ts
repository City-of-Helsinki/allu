import {AfterViewInit, ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit} from '@angular/core';
import {UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {Contact} from '@model/customer/contact';
import {Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {Subject} from 'rxjs/index';
import {SetContact, RemoveContact} from '@feature/information-request/actions/information-request-result-actions';
import {takeUntil} from 'rxjs/internal/operators';
import {InformationRequestFieldKey} from '@model/information-request/information-request-field-key';
import {config as acceptanceConfig} from '@feature/information-request/acceptance/customer/customer-acceptance-config';

@Component({
  selector: 'contacts-acceptance',
  templateUrl: './contacts-acceptance-component.html',
  styleUrls: ['./contacts-acceptance-component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ContactsAcceptanceComponent implements OnInit, OnDestroy, AfterViewInit {
  @Input() newContacts: Contact[] | undefined = undefined;
  @Input() oldContacts: Contact[] = [];
  @Input() parentForm: UntypedFormGroup;
  @Input() readonly: boolean;
  @Input() fieldKey: InformationRequestFieldKey;
  @Input() hideExisting = false;

  contactForms: UntypedFormArray;
  removedContacts: Contact[] = [];
  removedContactForm: UntypedFormGroup;
  contactChanges$ = new Subject<Contact>();
  removedContactChoices$ = new Subject<{ contactId: number; keep: boolean }>();

  private destroy: Subject<boolean> = new Subject<boolean>();

  constructor(private fb: UntypedFormBuilder,
              private store: Store<fromRoot.State>) {}

  ngOnInit(): void {
    const config = acceptanceConfig[this.fieldKey];
    const formName = `${config.formName}-contacts`;
    this.contactForms = this.fb.array([]);
    this.parentForm.addControl(formName, this.contactForms);

    // Detect removed contacts: only when newContacts is defined (i.e. the field was included
    // in the offered data). If newContacts is undefined the external system provided no contact
    // data at all for this role, so we cannot determine removals.
    const newContacts = this.newContacts;
    this.removedContacts = newContacts !== undefined
      ? this.oldContacts.filter(old => !newContacts.some(n => this.contactsMatch(n, old)))
      : [];

    // If there are removed contacts, add a form group to track decisions
    if (this.removedContacts.length > 0) {
      const removedContactsFormName = `${config.formName}-removed-contacts`;
      this.removedContactForm = this.fb.group({
        // Each removed contact gets a required form control
        ...this.removedContacts.reduce((acc, contact) => ({
          ...acc,
          [`contact-removal-${contact.id}`]: [null, Validators.required]
        }), {})
      });
      this.parentForm.addControl(removedContactsFormName, this.removedContactForm);
    }
  }

  ngOnDestroy(): void {
    if (this.removedContacts.length > 0 && this.removedContactForm) {
      const config = acceptanceConfig[this.fieldKey];
      const removedContactsFormName = `${config.formName}-removed-contacts`;
      this.parentForm.removeControl(removedContactsFormName);
    }
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  ngAfterViewInit(): void {
    this.contactChanges$.pipe(
      takeUntil(this.destroy)
    ).subscribe(contact => this.store.dispatch(new SetContact(contact)));

    this.removedContactChoices$.pipe(
      takeUntil(this.destroy)
    ).subscribe(choice => {
      if (!choice.keep) {
        // Handler chose to remove the contact
        this.store.dispatch(new RemoveContact(choice.contactId));
      }
      // Mark the form control as resolved
      if (this.removedContactForm) {
        this.removedContactForm.get(`contact-removal-${choice.contactId}`).setValue(true);
      }
    });
  }

  onContactChange(contact: Contact) {
    this.contactChanges$.next(contact);
  }

  onRemovedContactKeep(contactId: number): void {
    this.removedContactChoices$.next({ contactId, keep: true });
  }

  onRemovedContactRemove(contactId: number): void {
    this.removedContactChoices$.next({ contactId, keep: false });
  }

  /**
   * Match a new (external) contact against an old (internal) contact.
   * Prefer ID matching when the new contact has a real ID; fall back to
   * name-based matching for contacts that arrive from the external API
   * without internal IDs (id === null).
   */
  private contactsMatch(newContact: Contact, oldContact: Contact): boolean {
    if (newContact.id != null) {
      return newContact.id === oldContact.id;
    }
    // External contacts don't carry internal IDs — match by name (case-insensitive)
    return newContact.name?.trim().toLowerCase() === oldContact.name?.trim().toLowerCase();
  }
}
