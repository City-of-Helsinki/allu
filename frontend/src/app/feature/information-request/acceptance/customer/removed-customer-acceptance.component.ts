import {Component, Input, Output, EventEmitter, OnInit, OnDestroy} from '@angular/core';
import {select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {Customer} from '@model/customer/customer';
import {CodeSetCodeMap} from '@model/codeset/codeset';
import {Observable, Subject} from 'rxjs';
import {takeUntil} from 'rxjs/operators';
import {InformationRequestFieldKey} from '@model/information-request/information-request-field-key';
import {FieldDescription} from '@feature/information-request/acceptance/field-select/field-description';
import {FieldValues} from '@feature/information-request/acceptance/field-select/field-select.component';
import {findTranslation} from '@util/translations';
import {Some} from '@util/option';

@Component({
  selector: 'removed-customer-acceptance',
  templateUrl: './removed-customer-acceptance.component.html',
  styleUrls: ['./removed-customer-acceptance.component.scss']
})
export class RemovedCustomerAcceptanceComponent implements OnInit, OnDestroy {
  @Input() customer: Customer;
  @Input() fieldKey: InformationRequestFieldKey;
  @Output() keep = new EventEmitter<void>();
  @Output() remove = new EventEmitter<void>();

  removalAccepted: boolean = null;

  countryCodes$: Observable<CodeSetCodeMap>;
  private countryCodes: CodeSetCodeMap = {};
  private destroy = new Subject<boolean>();

  constructor(private store: Store<fromRoot.State>) {}

  ngOnInit(): void {
    this.countryCodes$ = this.store.pipe(select(fromRoot.getCodeSetCodeMap('Country')));
    this.countryCodes$.pipe(
      takeUntil(this.destroy)
    ).subscribe(codes => { this.countryCodes = codes || {}; });
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  keepCustomer(): void {
    if (this.removalAccepted === false) { return; }
    this.removalAccepted = false;
    this.keep.emit();
  }

  acceptRemoval(): void {
    if (this.removalAccepted === true) { return; }
    this.removalAccepted = true;
    this.remove.emit();
  }

  get customerDescriptions(): FieldDescription[] {
    const type = this.customer ? this.customer.type : undefined;
    return [
      new FieldDescription('type', findTranslation('customer.search.type')),
      new FieldDescription('name', findTranslation(type ? ['customer.type', type, 'nameLabel'] : 'name')),
      new FieldDescription('registryKey', findTranslation(type ? ['customer.type', type, 'id'] : 'customer.registryKey')),
      new FieldDescription('ovt', findTranslation(type ? ['customer.type', type, 'ovt'] : 'customer.ovt')),
      new FieldDescription('invoicingOperator', findTranslation(type ? ['customer.type', type, 'invoicingOperator'] : 'customer.invoicingOperator')),
      new FieldDescription('streetAddress', findTranslation('postalAddress.streetAddress')),
      new FieldDescription('postalCode', findTranslation('postalAddress.postalCode')),
      new FieldDescription('city', findTranslation('postalAddress.postalOffice')),
      new FieldDescription('email', findTranslation('customer.email')),
      new FieldDescription('phone', findTranslation('customer.phone')),
      new FieldDescription('country', findTranslation('customer.country')),
      new FieldDescription('sapCustomerNumber', findTranslation('customer.sapCustomerNumber'))
    ];
  }

  get customerFieldValues(): FieldValues {
    if (!this.customer) { return {}; }
    const c = this.customer;
    const postalAddress = c.postalAddress;
    return {
      type: c.type ? findTranslation(['customer.type', c.type, 'name']) : undefined,
      name: c.name,
      registryKey: c.registryKey,
      ovt: c.ovt,
      invoicingOperator: c.invoicingOperator,
      streetAddress: postalAddress ? postalAddress.streetAddress : undefined,
      postalCode: postalAddress ? postalAddress.postalCode : undefined,
      city: postalAddress ? postalAddress.city : undefined,
      email: c.email,
      phone: c.phone,
      country: Some(c.country)
        .map(code => this.countryCodes[code])
        .map(codeSet => codeSet.description)
        .orElse(c.country),
      sapCustomerNumber: c.sapCustomerNumber
    };
  }
}
