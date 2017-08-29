import {async, ComponentFixture, fakeAsync, TestBed} from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {FormBuilder, FormGroup, ReactiveFormsModule} from '@angular/forms';
import {MdCardModule} from '@angular/material';

import {CustomerInfoComponent} from '../../../../src/feature/customerregistry/customer/customer-info.component';
import {AlluCommonModule} from '../../../../src/feature/common/allu-common.module';
import {CustomerHub} from '../../../../src/service/customer/customer-hub';
import {CustomerType} from '../../../../src/model/customer/customer-type';

class CustomerHubMock {
  searchCustomersByField(fieldName: string, term: string) {}
}

describe('CustomerInfoComponent', () => {
  let comp: CustomerInfoComponent;
  let fixture: ComponentFixture<CustomerInfoComponent>;
  let page: CustomerInfoPage;
  let fb = new FormBuilder();

  class CustomerInfoPage {
    countryInput: HTMLInputElement;
    customerNameInput: HTMLInputElement;
    registryKeyInput: HTMLInputElement;
    customerAddressInput: HTMLInputElement;
    customerPostalCodeInput: HTMLInputElement;
    customerCityInput: HTMLInputElement;
    customerPhoneInput: HTMLInputElement;
    customerEmailInput: HTMLInputElement;

    addPageElements() {
      let debugElement = fixture.debugElement;
      this.countryInput = debugElement.query(By.css('[formControlName="country"]')).nativeElement;
      this.customerNameInput = debugElement.query(By.css('[formControlName="name"]')).nativeElement;
      this.registryKeyInput = debugElement.query(By.css('[formControlName="registryKey"]')).nativeElement;
      this.customerAddressInput = debugElement.query(By.css('[formControlName="streetAddress"]')).nativeElement;
      this.customerPostalCodeInput = debugElement.query(By.css('[formControlName="postalCode"]')).nativeElement;
      this.customerCityInput = debugElement.query(By.css('[formControlName="city"]')).nativeElement;
      this.customerPhoneInput = debugElement.query(By.css('[formControlName="phone"]')).nativeElement;
      this.customerEmailInput = debugElement.query(By.css('[formControlName="email"]')).nativeElement;
    }
  }

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [AlluCommonModule, ReactiveFormsModule, MdCardModule],
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
    comp.form = new FormGroup({});
    page = new CustomerInfoPage();
    page.addPageElements();
  });

  it('should show input forms values', fakeAsync(() => {
    let customerForm = fb.group({
      type: [CustomerType[CustomerType.PERSON]],
      name: ['NameTest'],
      registryKey: ['12345'],
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
    fixture.whenStable().then(result => {
      expect(page.customerNameInput.value).toEqual(customerForm.value.name);
      expect(page.registryKeyInput.value).toEqual(customerForm.value.registryKey);
      expect(page.customerAddressInput.value).toEqual(customerForm.value.postalAddress.streetAddress);
      expect(page.customerPostalCodeInput.value).toEqual(customerForm.value.postalAddress.postalCode);
      expect(page.customerCityInput.value).toEqual(customerForm.value.postalAddress.city);
      expect(page.customerPhoneInput.value).toEqual(customerForm.value.phone);
      expect(page.customerEmailInput.value).toEqual(customerForm.value.email);
    });
  }));
});
