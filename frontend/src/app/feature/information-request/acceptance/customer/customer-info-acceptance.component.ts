import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FieldLabels, FieldValues} from '../field-group-acceptance.component';
import {Customer} from '../../../../model/customer/customer';
import {PostalAddress} from '../../../../model/common/postal-address';
import {FormGroup} from '@angular/forms';
import {FieldSelection, Selected} from '../field-acceptance.component';
import {map} from 'rxjs/internal/operators';
import {findTranslation} from '../../../../util/translations';
import {CodeSetCodeMap} from '../../../../model/codeset/codeset';
import {Some} from '../../../../util/option';
import {InfoAcceptance} from '@feature/information-request/acceptance/info-acceptance';

@Component({
  selector: 'customer-info-acceptance',
  templateUrl: './customer-info-acceptance.component.html',
  styleUrls: []
})
export class CustomerInfoAcceptanceComponent extends InfoAcceptance<Customer> implements OnInit {
  _oldCustomer: Customer;
  _newCustomer: Customer;

  @Input() countryCodes: CodeSetCodeMap;
  @Input() readonly: boolean;

  @Output() customerChanges: EventEmitter<Customer> = new EventEmitter<Customer>();

  @Input() set oldCustomer(customer: Customer) {
    this._oldCustomer = customer;
    this.oldValues = this.toFieldValues(customer);
    this.oldDisplayValues = this.toDisplayValues(this.oldValues);
  }

  @Input() set newCustomer(customer: Customer) {
    this._newCustomer = customer;
    this.newValues = this.toFieldValues(customer);
    this.newDisplayValues = this.toDisplayValues(this.newValues);
    this.fieldLabels = this.createLabels(customer.type);
  }

  resultChanges(result: FieldValues): void {
    const customer = {...this._oldCustomer};
    customer.type = result.type;
    customer.name = result.name;
    customer.registryKey = result.registryKey;
    customer.ovt = result.ovt;
    customer.invoicingOperator = result.invoicingOperator;
    customer.postalAddress = new PostalAddress(result.streetAddress, result.postalCode, result.city);
    customer.email = result.email;
    customer.phone = result.phone;
    customer.country = result.country;
    this.customerChanges.emit(customer);
  }

  private toFieldValues(customer: Customer): FieldValues {
    if (customer) {
      const postalAddress = customer.postalAddress;
      return {
        type: customer.type,
        name: customer.name,
        registryKey: customer.registryKey,
        ovt: customer.ovt,
        invoicingOperator: customer.invoicingOperator,
        streetAddress: postalAddress.streetAddress,
        postalCode: postalAddress.postalCode,
        city: postalAddress.city,
        email: customer.email,
        phone: customer.phone,
        country: customer.country
      };
    } else {
      return {};
    }
  }

  private toDisplayValues(fieldValues: FieldValues): FieldValues {
    return {
      ...fieldValues,
      type: fieldValues.type ? findTranslation(['customer.type', fieldValues.type, 'name']) : undefined,
      country: this.getCountry(fieldValues.country)
    };
  }

  private createLabels(customerType: string): FieldLabels {
    return {
      type: findTranslation('customer.type.title'),
      name: findTranslation(['customer.type', customerType, 'nameLabel']),
      registryKey: findTranslation(['customer.type', customerType, 'id']),
      ovt: findTranslation(['customer.type', customerType, 'ovt']),
      invoicingOperator: findTranslation(['customer.type', customerType, 'invoicingOperator']),
      streetAddress: findTranslation('postalAddress.streetAddress'),
      postalCode: findTranslation('postalAddress.postalCode'),
      city: findTranslation('postalAddress.postalOffice'),
      email: findTranslation('customer.email'),
      phone: findTranslation('customer.phone'),
      country: findTranslation('customer.country')
    };
  }

  private getCountry(code: string): string {
    return Some(code)
      .map(c => this.countryCodes[c])
      .map(codeSet => codeSet.description)
      .orElse(undefined);
  }
}
