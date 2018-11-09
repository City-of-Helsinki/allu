import {AfterViewInit, ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormGroup} from '@angular/forms';
import {Contact} from '@model/customer/contact';
import {Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {Subject} from 'rxjs/index';
import {SetContact, SetContacts} from '@feature/information-request/actions/information-request-result-actions';
import {takeUntil} from 'rxjs/internal/operators';

@Component({
  selector: 'contacts-acceptance',
  templateUrl: './contacts-acceptance-component.html',
  styleUrls: ['./contacts-acceptance-component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ContactsAcceptanceComponent implements OnInit, OnDestroy, AfterViewInit {
  @Input() newContacts: Contact[] = [];
  @Input() parentForm: FormGroup;
  @Input() readonly: boolean;

  contactForms: FormArray;
  contactChanges$ = new Subject<{contact: Contact, index: number}>();

  private destroy: Subject<boolean> = new Subject<boolean>();

  constructor(private fb: FormBuilder,
              private store: Store<fromRoot.State>) {}

  ngOnInit(): void {
    this.contactForms = this.fb.array([]);
    this.parentForm.addControl('contacts', this.contactForms);
    this.store.dispatch(new SetContacts(this.newContacts));
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  ngAfterViewInit(): void {
    this.contactChanges$.pipe(
      takeUntil(this.destroy)
    ).subscribe(change => {
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
