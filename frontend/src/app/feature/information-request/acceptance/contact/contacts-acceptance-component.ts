import {AfterViewInit, Component, Input, OnDestroy, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormGroup} from '@angular/forms';
import {Contact} from '@model/customer/contact';
import {Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {Subject} from 'rxjs/index';
import {SetContact, SetContacts} from '@feature/information-request/actions/information-request-result-actions';

@Component({
  selector: 'contacts-acceptance',
  templateUrl: './contacts-acceptance-component.html'
})
export class ContactsAcceptanceComponent implements OnInit, OnDestroy, AfterViewInit {
  @Input() newContacts: Contact[] = [];
  @Input() parentForm: FormGroup;

  contactForms: FormArray;
  contactChanges$ = new Subject<{contact: Contact, index: number}>();

  constructor(private fb: FormBuilder,
              private store: Store<fromRoot.State>) {}

  ngOnInit(): void {
    this.contactForms = this.fb.array([]);
    this.parentForm.addControl('contacts', this.contactForms);
    this.store.dispatch(new SetContacts(this.newContacts));
  }

  ngOnDestroy(): void {
    this.contactChanges$.unsubscribe();
  }

  ngAfterViewInit(): void {
    this.contactChanges$.subscribe(change => {
      this.store.dispatch(new SetContact({
        contact: change.contact,
        index: change.index
      }));
    });
  }

  onContactChange(contact: Contact, index) {
    this.contactChanges$.next({contact, index});
  }
}
