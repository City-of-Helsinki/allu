import { ComponentFixture, fakeAsync, TestBed, tick, waitForAsync } from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {UntypedFormBuilder, UntypedFormGroup, ReactiveFormsModule} from '@angular/forms';
import {MatLegacyCardModule as MatCardModule} from '@angular/material/legacy-card';

import {CustomerInfoComponent} from '../../../../app/feature/customerregistry/customer/customer-info.component';
import {AlluCommonModule} from '../../../../app/feature/common/allu-common.module';
import {CustomerType} from '../../../../app/model/customer/customer-type';
import {DebugElement} from '@angular/core';
import {CustomerService} from '../../../../app/service/customer/customer.service';
import {CodeSetService} from '../../../../app/service/codeset/codeset.service';
import {CodeSet} from '../../../../app/model/codeset/codeset';
import {Observable, of} from 'rxjs/index';
import {CustomerOptionContentComponent} from '@feature/customerregistry/customer/customer-option-content.component';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { CurrentUser } from '@app/service/user/current-user';
import { CurrentUserMock } from 'test/mocks';

class CustomerHubMock {
  searchCustomersByField(fieldName: string, term: string) {}
}
class CodeSetServiceMock {
  public getCountries(): Observable<Array<CodeSet>> {
    return of([{code: 'FI', type: 'Country', description: 'Suomi'}]);
  }
}

describe('CustomerInfoComponent', () => {
  let comp: CustomerInfoComponent;
  let fixture: ComponentFixture<CustomerInfoComponent>;
  let page: CustomerInfoPage;
  const fb = new UntypedFormBuilder();
  let customerForm: UntypedFormGroup;
  let debugElement: DebugElement;

  class CustomerInfoPage {
    countryInput: HTMLInputElement;
    customerNameInput: HTMLInputElement;
    registryKeyInput: HTMLInputElement;
    ovtInput: HTMLInputElement;
    invoicingOperatorInput: HTMLInputElement;
    customerAddressInput: HTMLInputElement;
    customerPostalCodeInput: HTMLInputElement;
    customerCityInput: HTMLInputElement;
    customerPhoneInput: HTMLInputElement;
    customerEmailInput: HTMLInputElement;

    addPageElements() {
      this.countryInput = debugElement.query(By.css('[formControlName="country"]')).nativeElement;
      this.customerNameInput = debugElement.query(By.css('[formControlName="name"]')).nativeElement;
      this.registryKeyInput = debugElement.query(By.css('[formControlName="registryKey"]')).nativeElement;
      this.ovtInput = debugElement.query(By.css('[formControlName="ovt"]')).nativeElement;
      this.invoicingOperatorInput = debugElement.query(By.css('[formControlName="invoicingOperator"]')).nativeElement;
      this.customerAddressInput = debugElement.query(By.css('[formControlName="streetAddress"]')).nativeElement;
      this.customerPostalCodeInput = debugElement.query(By.css('[formControlName="postalCode"]')).nativeElement;
      this.customerCityInput = debugElement.query(By.css('[formControlName="city"]')).nativeElement;
      this.customerPhoneInput = debugElement.query(By.css('[formControlName="phone"]')).nativeElement;
      this.customerEmailInput = debugElement.query(By.css('[formControlName="email"]')).nativeElement;
    }
  }

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [AlluCommonModule, ReactiveFormsModule, MatCardModule, NoopAnimationsModule],
      declarations: [CustomerInfoComponent, CustomerOptionContentComponent],
      providers: [
        {provide: CustomerService, useClass: CustomerHubMock},
        {provide: CodeSetService, useClass: CodeSetServiceMock},
        {provide: CurrentUser, useClass: CurrentUserMock}
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CustomerInfoComponent);
    comp = fixture.componentInstance;
    debugElement = fixture.debugElement;

    customerForm = fb.group({
      type: [CustomerType.COMPANY],
      name: ['NameTest'],
      registryKey: ['12345'],
      ovt: ['003712345'],
      invoicingOperator: ['IO123'],
      country: 'FI',
      postalAddress: fb.group({
        streetAddress: ['streetAddressTest'],
        postalCode: ['postalCodeTest'],
        city: ['cityTest']
      }),
      phone: ['phoneTest'],
      email: ['emailTest'],
      invoicingOnly: [true],
      projectIdentifierPrefix: []
    });

    comp.form = customerForm;
    comp.showInvoicingOnly = true;
    comp.ngOnInit();
    fixture.detectChanges();
    page = new CustomerInfoPage();
    page.addPageElements();
  });

  // it('should show input forms values', fakeAsync(() => {
  //   fixture.whenStable().then(result => {
  //     page.addPageElements();
  //     expect(page.customerNameInput.value).toEqual(customerForm.value.name);
  //     expect(page.registryKeyInput.value).toEqual(customerForm.value.registryKey);
  //     expect(page.ovtInput.value).toEqual(customerForm.value.ovt);
  //     expect(page.invoicingOperatorInput.value).toEqual(customerForm.value.invoicingOperator);
  //     expect(page.customerAddressInput.value).toEqual(customerForm.value.postalAddress.streetAddress);
  //     expect(page.customerPostalCodeInput.value).toEqual(customerForm.value.postalAddress.postalCode);
  //     expect(page.customerCityInput.value).toEqual(customerForm.value.postalAddress.city);
  //     expect(page.customerPhoneInput.value).toEqual(customerForm.value.phone);
  //     expect(page.customerEmailInput.value).toEqual(customerForm.value.email);
  //     expect(debugElement.query(By.css('[formControlName="invoicingOnly"] .mat-checkbox-checked'))).toBeDefined();
  //   });
  // }));

  // it('should hide ovt for person type', fakeAsync(() => {
  //   customerForm.patchValue({type: 'PERSON'});
  //   fixture.detectChanges();
  //   tick();
  //   expect(debugElement.query(By.css('[formControlName="ovt"]'))).toBeFalsy();
  // }));
});
