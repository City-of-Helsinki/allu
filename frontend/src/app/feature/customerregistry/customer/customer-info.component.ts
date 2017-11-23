import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {CustomerType} from '../../../model/customer/customer-type';
import {EnumUtil} from '../../../util/enum.util';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {Observable} from 'rxjs/Observable';
import {Subscription} from 'rxjs/Subscription';
import {NumberUtil} from '../../../util/number.util';
import {CustomerHub} from '../../../service/customer/customer-hub';
import {CustomerForm} from './customer.form';
import {ComplexValidator} from '../../../util/complex-validator';
import {Customer} from '../../../model/customer/customer';
import {CustomerSearchQuery} from '../../../service/mapper/query/customer-query-parameters-mapper';

export const ALWAYS_ENABLED_FIELDS = ['id', 'type', 'name', 'registryKey', 'representative'];
const REGISTRY_KEY_VALIDATORS = [Validators.required, Validators.minLength(2)];
const PERSON_REGISTRY_KEY_VALIDATORS = [Validators.required, Validators.minLength(2), ComplexValidator.invalidSsnWarning];
const DEBOUNCE_TIME_MS = 300;

@Component({
  selector: 'customer-info',
  templateUrl: './customer-info.component.html',
  styleUrls: []
})
export class CustomerInfoComponent implements OnInit, OnDestroy {
  @Input() form: FormGroup;
  @Input() allowSearch = false;
  @Input() showInvoicingInfo = false;

  @Output() customerChange = new EventEmitter<Customer>();

  matchingNameCustomers: Observable<Array<Customer>>;
  matchingRegistryKeyCustomers: Observable<Array<Customer>>;
  customerTypes = EnumUtil.enumValues(CustomerType);
  typeSubscription: Subscription;
  registryKeyControl: FormControl;

  private nameControl: FormControl;
  private typeControl: FormControl;

  constructor(private customerHub: CustomerHub) {}

  ngOnInit() {
    this.nameControl = <FormControl>this.form.get('name');
    this.typeControl = <FormControl>this.form.get('type');
    this.registryKeyControl = <FormControl>this.form.get('registryKey');

    this.matchingNameCustomers = this.nameControl.valueChanges
      .debounceTime(DEBOUNCE_TIME_MS)
      .switchMap(name => this.onSearchChange({name: name}));

    this.matchingRegistryKeyCustomers = this.registryKeyControl.valueChanges
      .debounceTime(DEBOUNCE_TIME_MS)
      .switchMap(key => this.onSearchChange({registryKey: key}));

    this.typeSubscription = this.typeControl.valueChanges
      .map((type: string) => CustomerType[type])
      .subscribe(type => this.updateRegistryKeyValidators(type));
  }

  ngOnDestroy(): void {
    this.typeSubscription.unsubscribe();
  }

  onSearchChange(terms: CustomerSearchQuery): Observable<Array<Customer>> {
    if (this.allowSearch) {
      const termsWithType = {...terms, type: this.typeControl.value};
      return this.customerHub.searchCustomersBy(termsWithType);
    } else {
      return Observable.empty();
    }
  }

  customerSelected(customer: Customer): void {
    this.form.patchValue(CustomerForm.fromCustomer(customer));
    this.customerChange.emit(customer);
  }

  /**
   * Resets form values if form contained existing customer
   * and form allows search
   */
  resetFormIfExisting(): void {
    if (NumberUtil.isDefined(this.form.value.id) && this.allowSearch) {
      this.form.reset({
        name: this.form.value.name,
        type: this.form.value.type,
        active: true
      });
      this.form.enable();
      this.customerChange.emit(new Customer());
    }
  }

  get existingCustomer(): boolean {
    return NumberUtil.isDefined(this.form.value.id);
  }

  private updateRegistryKeyValidators(type: CustomerType): void {
    if (type === CustomerType.PERSON) {
      this.registryKeyControl.setValidators(PERSON_REGISTRY_KEY_VALIDATORS);
    } else {
      this.registryKeyControl.setValidators(REGISTRY_KEY_VALIDATORS);
    }
  }
}
