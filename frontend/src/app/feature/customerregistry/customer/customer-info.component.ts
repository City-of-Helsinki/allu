import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {CustomerType} from '@model/customer/customer-type';
import {EnumUtil} from '@util/enum.util';
import {FormBuilder, FormGroup, UntypedFormControl, UntypedFormGroup, Validators} from '@angular/forms';
import {EMPTY, Observable, Subject, Subscription} from 'rxjs';
import {NumberUtil} from '@util/number.util';
import {CustomerForm} from './customer.form';
import {ComplexValidator} from '@util/complex-validator';
import {Customer} from '@model/customer/customer';
import {CustomerService} from '@service/customer/customer.service';
import {
  CustomerNameSearchMinChars,
  CustomerRegistryKeySearchMinChars,
  CustomerSearchQuery
} from '@service/customer/customer-search-query';
import {CodeSetService} from '@service/codeset/codeset.service';
import {CodeSet} from '@model/codeset/codeset';
import {postalCodeValidator} from '@util/complex-validator';
import {debounceTime, filter, map, startWith, switchMap, take, takeUntil} from 'rxjs/internal/operators';
import { CurrentUser } from '@app/service/user/current-user';
import {RoleType} from '@model/user/role-type';
import { Router } from '@angular/router'

export const ALWAYS_ENABLED_FIELDS = ['id', 'type', 'name', 'registryKey', 'representative'];
const REGISTRY_KEY_VALIDATORS = [Validators.minLength(2)];
const PERSON_REGISTRY_KEY_VALIDATORS = [Validators.minLength(2), ComplexValidator.invalidSsnWarning];
const DEBOUNCE_TIME_MS = 300;

@Component({
  selector: 'customer-info',
  templateUrl: './customer-info.component.html',
  styleUrls: []
})
export class CustomerInfoComponent implements OnInit, OnDestroy {
  @Input() form: FormGroup;
  @Input() allowSearch = true;
  @Input() showInvoicingInfo = false;
  @Input() showInvoicingOnly = false;
  @Input() showInvoicingProhibited = false;
  @Input() excludeInvoicingOnlyCustomers = true;

  @Output() customerChange = new EventEmitter<Customer>();

  matchingNameCustomers: Observable<Array<Customer>>;
  matchingRegistryKeyCustomers: Observable<Array<Customer>>;
  countries: Observable<Array<CodeSet>>;
  customerTypes = EnumUtil.enumValues(CustomerType);
  typeSubscription: Subscription;
  registryKeyControl: UntypedFormControl;
  countrySubscription: Subscription;

  private nameControl: UntypedFormControl;
  private typeControl: UntypedFormControl;
  private countryControl: UntypedFormControl;
  private postalCodeControl: UntypedFormControl;
  private destroy$ = new Subject<void>();

  customerInstance: Customer;


  constructor(private customerService: CustomerService, private codeSetService: CodeSetService, private currentUser: CurrentUser, private router: Router) {}

  ngOnInit() {
    this.nameControl = <UntypedFormControl>this.form.get('name');
    this.typeControl = <UntypedFormControl>this.form.get('type');
    this.registryKeyControl = <UntypedFormControl>this.form.get('registryKey');
    this.countryControl = <UntypedFormControl>this.form.get('country');
    this.postalCodeControl = <UntypedFormControl> this.form.get('postalAddress').get('postalCode');
    this.customerInstance = new Customer();
    

    this.matchingNameCustomers = this.nameControl.valueChanges.pipe(
      debounceTime(DEBOUNCE_TIME_MS),
      filter(CustomerNameSearchMinChars),
      switchMap(name => this.onSearchChange({name: name}))
    );

    this.matchingRegistryKeyCustomers = this.registryKeyControl.valueChanges.pipe(
      debounceTime(DEBOUNCE_TIME_MS),
      filter(CustomerRegistryKeySearchMinChars),
      switchMap(key => this.onSearchChange({registryKey: key}))
    );

    this.typeSubscription = this.typeControl.valueChanges
      .subscribe(type => this.updateRegistryKeyValidators(type));

    this.countrySubscription = this.countryControl.valueChanges
      .subscribe(country => this.updatePostalAddressValidator(country));

    this.countries = this.codeSetService.getCountries();
    
    this.form.get("id").valueChanges
      .pipe(
        startWith(this.form.get('id').value),
        takeUntil(this.destroy$)
      )
      .subscribe(id => this.handleFormLockStatus(id));

    // in customerregistery only editing should happen in the customerform
    if (this.router.url.includes('/customers/')) this.allowSearch = false;
  }

  ngOnDestroy(): void {
    this.typeSubscription.unsubscribe();
    this.countrySubscription.unsubscribe();
    this.destroy$.next();
    this.destroy$.complete();
  }

  async handleFormLockStatus(id: number): Promise<void> {
    const currentRoute = this.router.url;

    // // if were in edit and have no customer, unlock the customer form
    if (currentRoute.endsWith('/edit/info') && !id) {
      if (this.form.disabled) this.form.enable();
    }
    // // inside customer, form should be editable for roles with proper permissions
    else if (currentRoute.includes('/customer')) {
      await this.checkForRestrictedEdit();
    }
    // disable form in the summary view 
    if (currentRoute.endsWith('/summary/info') && id) {
      if (this.form.enabled) this.form.disable();
    }
    // enable form by default
    else {
      if (this.form.disabled) this.form.enable();
    }
  }

  //this check should only happen inside /customer
  async checkForRestrictedEdit(): Promise<void> {
    if (this.form.disabled) this.form.enable();
    const userHasRole = await this.currentUser.hasRole([RoleType.ROLE_INVOICING, RoleType.ROLE_ADMIN].map(role => RoleType[role])).toPromise();
    this.form.get("sapCustomerNumber").valueChanges
      .pipe(take(1))
      .subscribe(value => {
        // if there exists sapNumber but user doesn't have role. disable
        if (value && !userHasRole) this.form.disable();
      });

  }

  onSearchChange(terms: CustomerSearchQuery): Observable<Array<Customer>> {
    if (this.allowSearch) {
      const termsWithType = {...terms, type: this.typeControl.value, active: true};
      if (this.excludeInvoicingOnlyCustomers) {
        termsWithType['invoicingOnly'] = false;
      }
      return this.customerService.search(termsWithType);
    } else {
      return EMPTY;
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
        active: true,
        country: 'FI'
      });
      this.form.enable();
      this.customerChange.emit(new Customer());
    }
  }

  onKeyup(event: KeyboardEvent): void {
    if (event.code !== 'Enter') {
      this.resetFormIfExisting();
    }
  }

  get existingCustomer(): boolean {
    return NumberUtil.isDefined(this.form.value.id);
  }

  private updatePostalAddressValidator(country: string): void {
    if (country === 'FI') {
      this.postalCodeControl.setValidators(postalCodeValidator);
    } else {
      this.postalCodeControl.setValidators([]);
    }
    this.postalCodeControl.updateValueAndValidity();
  }

  private updateRegistryKeyValidators(type: CustomerType): void {
    if (type === CustomerType.PERSON) {
      this.registryKeyControl.setValidators(PERSON_REGISTRY_KEY_VALIDATORS);
    } else {
      this.registryKeyControl.setValidators(REGISTRY_KEY_VALIDATORS);
    }
  }

  get invoicingProhibited(): boolean {
    return (<CustomerForm>this.form.getRawValue()).invoicingProhibited;
  }
}
