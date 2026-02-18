import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Customer} from '@model/customer/customer';
import {PostalAddress} from '@model/common/postal-address';
import {findTranslation} from '@util/translations';
import {CodeSetCodeMap} from '@model/codeset/codeset';
import {Some} from '@util/option';
import {InfoAcceptanceDirective} from '@feature/information-request/acceptance/info-acceptance/info-acceptance.component';
import {UntypedFormBuilder, Validators} from '@angular/forms';
import {FieldValues} from '@feature/information-request/acceptance/field-select/field-select.component';
import {FieldDescription} from '@feature/information-request/acceptance/field-select/field-description';

const requiredFields = {
  type: true,
  name: true,
  registryKey: true
};

@Component({
  selector: 'customer-info-acceptance',
  templateUrl: '../info-acceptance/info-acceptance.component.html',
  styleUrls: ['../info-acceptance/info-acceptance.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CustomerInfoAcceptanceComponent extends InfoAcceptanceDirective<Customer> implements OnInit {
  _oldCustomer: Customer;
  _newCustomer: Customer;

  constructor(fb: UntypedFormBuilder) {
    super(fb);
  }

  @Input() countryCodes: CodeSetCodeMap;

  @Output() customerChanges: EventEmitter<Customer> = new EventEmitter<Customer>();

  @Input() set oldCustomer(customer: Customer) {
    this._oldCustomer = customer;
    this.oldValues = this.toFieldValues(customer);
    this.oldDisplayValues = this.toDisplayValues(this.oldValues);

    // Customer id is set from old customer since we should only allow saving form when
    // customer with id is selected as reference customer
    if (customer) {
      this.form.patchValue({id: customer.id});
      this.selectAllOld();
    } else {
      this.clearSelections();
    }
  }

  @Input() set newCustomer(customer: Customer) {
    this._newCustomer = customer;
    this.newValues = this.toFieldValues(customer);
    this.newDisplayValues = this.toDisplayValues(this.newValues);
    this.fieldDescriptions = this.createDescriptions(customer.type);
  }

  resultChanges(result: FieldValues): void {
    const customer = {...this._oldCustomer};
    customer.type = this._newCustomer.type || this._oldCustomer.type;
    customer.name = result.name;
    customer.registryKey = result.registryKey;
    customer.ovt = result.ovt;
    customer.invoicingOperator = result.invoicingOperator;
    customer.postalAddress = new PostalAddress(result.streetAddress, result.postalCode, result.city);
    customer.email = result.email;
    customer.phone = result.phone;
    customer.country = result.country;
    customer.active = this._newCustomer.active;
    customer.sapCustomerNumber = result.sapCustomerNumber;
    this.customerChanges.emit(customer);
  }

  protected initResultForm(): void {
    super.initResultForm();
    const customerId = this._oldCustomer ? this._oldCustomer.id : undefined;
    const ctrl = this.fb.control(customerId, Validators.required);
    this.form.addControl('id', ctrl);
  }

  protected isRequired(field: string): boolean {
    return requiredFields[field];
  }

  private toFieldValues(customer: Customer): FieldValues {
    if (customer) {
      const postalAddress = customer.postalAddress;
      return {
        name: customer.name,
        registryKey: customer.registryKey,
        ovt: customer.ovt,
        invoicingOperator: customer.invoicingOperator,
        streetAddress: postalAddress.streetAddress,
        postalCode: postalAddress.postalCode,
        city: postalAddress.city,
        email: customer.email,
        phone: customer.phone,
        country: customer.country,
        sapCustomerNumber: customer.sapCustomerNumber
      };
    } else {
      return {};
    }
  }

  private toDisplayValues(fieldValues: FieldValues): FieldValues {
    return {
      ...fieldValues,
      country: this.getCountry(fieldValues.country)
    };
  }

  private createDescriptions(customerType: string): FieldDescription[] {
    return [
      new FieldDescription('name', findTranslation(['customer.type', customerType, 'nameLabel'])),
      new FieldDescription('registryKey', findTranslation(['customer.type', customerType, 'id'])),
      new FieldDescription('ovt', findTranslation(['customer.type', customerType, 'ovt'])),
      new FieldDescription('invoicingOperator', findTranslation(['customer.type', customerType, 'invoicingOperator'])),
      new FieldDescription('streetAddress', findTranslation('postalAddress.streetAddress')),
      new FieldDescription('postalCode', findTranslation('postalAddress.postalCode')),
      new FieldDescription('city', findTranslation('postalAddress.postalOffice')),
      new FieldDescription('email', findTranslation('customer.email')),
      new FieldDescription('phone', findTranslation('customer.phone')),
      new FieldDescription('country', findTranslation('customer.country')),
      new FieldDescription('sapCustomerNumber', findTranslation('customer.sapCustomerNumber'))
    ];
  }

  private getCountry(code: string): string {
    return Some(code)
      .map(c => this.countryCodes[c])
      .map(codeSet => codeSet.description)
      .orElse(undefined);
  }
}
