import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {FormBuilder, FormGroup, ReactiveFormsModule} from '@angular/forms';
import {MatCardModule} from '@angular/material';

import {CustomerInfoComponent} from '../../../../src/app/feature/customerregistry/customer/customer-info.component';
import {AlluCommonModule} from '../../../../src/app/feature/common/allu-common.module';
import {CustomerHub} from '../../../../src/app/service/customer/customer-hub';
import {CustomerType} from '../../../../src/app/model/customer/customer-type';
import {DebugElement} from '@angular/core';

class CustomerHubMock {
  searchCustomersByField(fieldName: string, term: string) {}
}

describe('CustomerInfoComponent', () => {
  let comp: CustomerInfoComponent;
  let fixture: ComponentFixture<CustomerInfoComponent>;
  let page: CustomerInfoPage;
  let fb = new FormBuilder();
  let customerForm: FormGroup;
  let debugElement: DebugElement;

  class CustomerInfoPage {
    countryInput: HTMLInputElement;
    customerNameInput: HTMLInputElement;
    registryKeyInput: HTMLInputElement;
    ovtInput: HTMLInputElement;
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
      this.customerAddressInput = debugElement.query(By.css('[formControlName="streetAddress"]')).nativeElement;
      this.customerPostalCodeInput = debugElement.query(By.css('[formControlName="postalCode"]')).nativeElement;
      this.customerCityInput = debugElement.query(By.css('[formControlName="city"]')).nativeElement;
      this.customerPhoneInput = debugElement.query(By.css('[formControlName="phone"]')).nativeElement;
      this.customerEmailInput = debugElement.query(By.css('[formControlName="email"]')).nativeElement;
    }
  }

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [AlluCommonModule, ReactiveFormsModule, MatCardModule],
      declarations: [CustomerInfoComponent],
      providers: [
        {provide: FormBuilder, useValue: new FormBuilder()},
        {provide: CustomerHub, useClass: CustomerHubMock}
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CustomerInfoComponent);
    comp = fixture.componentInstance;
    debugElement = fixture.debugElement;

    customerForm = fb.group({
      type: [CustomerType[CustomerType.COMPANY]],
      name: ['NameTest'],
      registryKey: ['12345'],
      ovt: ['003712345'],
      country: undefined,
      postalAddress: fb.group({
        streetAddress: ['streetAddressTest'],
        postalCode: ['postalCodeTest'],
        city: ['cityTest']
      }),
      phone: ['phoneTest'],
      email: ['emailTest']
    });

    comp.form = customerForm;
    comp.ngOnInit();
    fixture.detectChanges();
    page = new CustomerInfoPage();
    page.addPageElements();
  });

  it('should show input forms values', fakeAsync(() => {
    fixture.whenStable().then(result => {
      page.addPageElements();
      expect(page.customerNameInput.value).toEqual(customerForm.value.name);
      expect(page.registryKeyInput.value).toEqual(customerForm.value.registryKey);
      expect(page.ovtInput.value).toEqual(customerForm.value.ovt);
      expect(page.customerAddressInput.value).toEqual(customerForm.value.postalAddress.streetAddress);
      expect(page.customerPostalCodeInput.value).toEqual(customerForm.value.postalAddress.postalCode);
      expect(page.customerCityInput.value).toEqual(customerForm.value.postalAddress.city);
      expect(page.customerPhoneInput.value).toEqual(customerForm.value.phone);
      expect(page.customerEmailInput.value).toEqual(customerForm.value.email);
    });
  }));

  it('should hide ovt for person type', fakeAsync(() => {
    customerForm.patchValue({type: 'PERSON'});
    fixture.detectChanges();
    tick();
    expect(debugElement.query(By.css('[formControlName="ovt"]'))).toBeFalsy();
  }));
});
