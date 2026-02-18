import {Component, Input, OnInit, OnDestroy} from '@angular/core';
import {UntypedFormGroup, UntypedFormBuilder, UntypedFormControl, Validators} from '@angular/forms';
import {CustomerWithContacts} from '@model/customer/customer-with-contacts';
import {InformationRequestFieldKey} from '@model/information-request/information-request-field-key';
import {Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {SetCustomer} from '@feature/information-request/actions/information-request-result-actions';
import {config as acceptanceConfig, CustomerAcceptanceConfig} from '@feature/information-request/acceptance/customer/customer-acceptance-config';

@Component({
  selector: 'customer-with-contacts-acceptance',
  templateUrl: './customer-with-contacts-acceptance.component.html',
  styleUrls: ['./customer-with-contacts-acceptance.component.scss']
})
export class CustomerWithContactsAcceptanceComponent implements OnInit, OnDestroy {
  @Input() oldCustomerWithContacts: CustomerWithContacts;
  @Input() newCustomerWithContacts: CustomerWithContacts;
  @Input() parentForm: UntypedFormGroup;
  @Input() readonly: boolean;
  @Input() fieldKey: InformationRequestFieldKey;
  @Input() canBeInvoiceRecipient = false;
  @Input() hideExisting = false;
  /** Fallback customer data from clientApplicationData, used when customersWithContacts has no entry for this role */
  @Input() fallbackCustomerWithContacts: CustomerWithContacts;

  customerRemoved = false;

  /** The effective old customer to display: prefer customersWithContacts, fall back to clientApplicationData */
  get effectiveOldCustomerWithContacts(): CustomerWithContacts {
    if (this.oldCustomerWithContacts?.customer) {
      return this.oldCustomerWithContacts;
    }
    return this.fallbackCustomerWithContacts;
  }
  /** Tracks the handler's choice: true = accept removal, false = keep old customer, null = no choice yet */
  removalAccepted: boolean = null;

  private removalFormName: string;
  private removalChoiceCtrl: UntypedFormControl;
  private cfg: CustomerAcceptanceConfig;

  constructor(
    private fb: UntypedFormBuilder,
    private store: Store<fromRoot.State>
  ) {}

  ngOnInit(): void {
    if (!this.newCustomerWithContacts || !this.newCustomerWithContacts.customer) {
      this.customerRemoved = true;
      this.cfg = acceptanceConfig[this.fieldKey];
      this.removalFormName = this.cfg ? this.cfg.formName : this.fieldKey;

      // Add a form control with required validator so the form stays invalid until the handler makes a choice
      this.removalChoiceCtrl = this.fb.control(null, Validators.required);
      const group = this.fb.group({removalChoice: this.removalChoiceCtrl});
      this.parentForm.addControl(this.removalFormName, group);
    }
  }

  /** Handler chooses to accept the removal (right side) */
  acceptRemoval(): void {
    if (this.removalAccepted === true) {
      return;
    }
    this.removalAccepted = true;
    this.removalChoiceCtrl.setValue('remove');
    if (this.cfg) {
      this.store.dispatch(new SetCustomer(this.cfg.actionTargetType, null));
    }
  }

  /** Handler chooses to keep the old customer (left side) */
  keepCustomer(): void {
    if (this.removalAccepted === false) {
      return;
    }
    this.removalAccepted = false;
    this.removalChoiceCtrl.setValue('keep');
    if (this.cfg) {
      // Dispatch undefined — result service will leave the existing customer untouched
      this.store.dispatch(new SetCustomer(this.cfg.actionTargetType, undefined));
    }
  }

  ngOnDestroy(): void {
    if (this.customerRemoved && this.removalFormName) {
      this.parentForm.removeControl(this.removalFormName);
    }
  }
}
