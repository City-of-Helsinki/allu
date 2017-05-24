import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {FormBuilder, FormGroup, ReactiveFormsModule} from '@angular/forms';
import {MdCardModule} from '@angular/material';

import {CustomerComponent} from '../../../../../src/feature/application/info/customer/customer.component';
import {AlluCommonModule} from '../../../../../src/feature/common/allu-common.module';
import {CustomerHub} from '../../../../../src/service/customer/customer-hub';
import {ContactComponent} from '../../../../../src/feature/application/info/contact/contact.component';
import {CustomerInfoComponent} from '../../../../../src/feature/customerregistry/customer/customer-info.component';
import {Customer} from '../../../../../src/model/customer/customer';
import {CustomerWithContacts} from '../../../../../src/model/customer/customer-with-contacts';
import {CustomerRoleType} from '../../../../../src/model/customer/customer-role-type';

const headerText = 'Hakija';

class CustomerHubMock {
  searchCustomersByField(fieldName: string, term: string) {}
}

describe('CustomerComponent', () => {
  let comp: CustomerComponent;
  let fixture: ComponentFixture<CustomerComponent>;
  let page: CustomerPage;

  class CustomerPage {
    cardTitle: HTMLElement;
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
      this.cardTitle = debugElement.query(By.css('md-card-title')).nativeElement;
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
      declarations: [CustomerComponent, ContactComponent, CustomerInfoComponent],
      providers: [
        {provide: FormBuilder, useValue: new FormBuilder()},
        {provide: CustomerHub, useClass: CustomerHubMock}
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CustomerComponent);
    comp = fixture.componentInstance;
    comp.parentForm = new FormGroup({});
    comp.customerWithContacts = new CustomerWithContacts(CustomerRoleType.APPLICANT);
    comp.readonly = false;
    page = new CustomerPage();
    page.addPageElements();
  });

  it('should show header text from input', () => {
    fixture.detectChanges();
    fixture.whenStable().then(result => {
      expect(page.cardTitle.textContent).toContain(headerText);
    });
  });

  it('should fill the form with input customer', () => {
    let customer = new Customer();
    customer.name = 'NameTest';
    customer.registryKey = '12345';
    customer.postalAddress.streetAddress = 'streetAddressTest';
    customer.postalAddress.postalCode = 'postalCodeTest';
    customer.postalAddress.city = 'cityTest';
    customer.phone = 'phoneTest';
    customer.email = 'emailTest';

    comp.customerWithContacts = new CustomerWithContacts(CustomerRoleType.APPLICANT, customer);
    fixture.detectChanges();
    fixture.whenStable().then(result => {
      expect(page.customerNameInput.value).toEqual(customer.name);
      expect(page.registryKeyInput.value).toEqual(customer.registryKey);
      expect(page.customerAddressInput.value).toEqual(customer.postalAddress.streetAddress);
      expect(page.customerPostalCodeInput.value).toEqual(customer.postalAddress.postalCode);
      expect(page.customerCityInput.value).toEqual(customer.postalAddress.city);
      expect(page.customerPhoneInput.value).toEqual(customer.phone);
      expect(page.customerEmailInput.value).toEqual(customer.email);
    });
  });

  it('should disable fields if readonly', () => {
    comp.readonly = true;
    fixture.detectChanges();
    fixture.whenStable().then(result => {
      expect(page.customerNameInput.disabled).toBeTruthy();
      expect(page.registryKeyInput.disabled).toBeTruthy();
      expect(page.customerAddressInput.disabled).toBeTruthy();
      expect(page.customerPostalCodeInput.disabled).toBeTruthy();
      expect(page.customerCityInput.disabled).toBeTruthy();
      expect(page.customerPhoneInput.disabled).toBeTruthy();
      expect(page.customerEmailInput.disabled).toBeTruthy();
    });
  });
});
