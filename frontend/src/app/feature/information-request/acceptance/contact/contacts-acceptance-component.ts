import {AfterViewInit, Component, Input, OnDestroy, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormGroup} from '@angular/forms';
import {Contact} from '@model/customer/contact';
import {Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import * as fromInformationRequest from '@feature/information-request/reducers';
import {Subject} from 'rxjs/index';
import {SetContact, SetContacts} from '@feature/information-request/actions/information-request-result-actions';
import {distinctUntilChanged, map, takeUntil} from 'rxjs/internal/operators';
import {NumberUtil} from '@util/number.util';
import {LoadByCustomer, LoadByCustomerSuccess} from '@feature/customerregistry/actions/contact-search-actions';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';

@Component({
  selector: 'contacts-acceptance',
  templateUrl: './contacts-acceptance-component.html',
  styleUrls: ['./contacts-acceptance-component.scss']
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

    this.store.select(fromInformationRequest.getResultCustomer).pipe(
      takeUntil(this.destroy),
      map(customer => customer ? customer.id : undefined),
      distinctUntilChanged()
    ).subscribe(id => {
      if (NumberUtil.isDefined(id)) {
        this.store.dispatch(new LoadByCustomer(ActionTargetType.Applicant, id));
      } else {
        this.store.dispatch(new LoadByCustomerSuccess(ActionTargetType.Applicant, []));
      }
    });
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
