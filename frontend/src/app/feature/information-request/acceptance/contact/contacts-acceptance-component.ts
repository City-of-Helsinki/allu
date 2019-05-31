import {AfterViewInit, ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormGroup} from '@angular/forms';
import {Contact} from '@model/customer/contact';
import {Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {Subject} from 'rxjs/index';
import {SetContact} from '@feature/information-request/actions/information-request-result-actions';
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
  @Input() newContacts: Contact[] = [];
  @Input() parentForm: FormGroup;
  @Input() readonly: boolean;
  @Input() fieldKey: InformationRequestFieldKey;

  contactForms: FormArray;
  contactChanges$ = new Subject<Contact>();

  private destroy: Subject<boolean> = new Subject<boolean>();

  constructor(private fb: FormBuilder,
              private store: Store<fromRoot.State>) {}

  ngOnInit(): void {
    const config = acceptanceConfig[this.fieldKey];
    const formName = `${config.formName}-contacts`;
    this.contactForms = this.fb.array([]);
    this.parentForm.addControl(formName, this.contactForms);
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  ngAfterViewInit(): void {
    this.contactChanges$.pipe(
      takeUntil(this.destroy)
    ).subscribe(contact => this.store.dispatch(new SetContact(contact)));
  }

  onContactChange(contact: Contact) {
    this.contactChanges$.next(contact);
  }
}
