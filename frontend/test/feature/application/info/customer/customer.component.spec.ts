import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {FormBuilder, FormGroup, ReactiveFormsModule} from '@angular/forms';
import {MatCardModule} from '@angular/material';

import {CustomerComponent} from '../../../../../src/feature/application/info/customer/customer.component';
import {AlluCommonModule} from '../../../../../src/feature/common/allu-common.module';
import {CustomerHub} from '../../../../../src/service/customer/customer-hub';
import {CustomerInfoComponent} from '../../../../../src/feature/customerregistry/customer/customer-info.component';
import {Customer} from '../../../../../src/model/customer/customer';
import {CustomerWithContacts} from '../../../../../src/model/customer/customer-with-contacts';
import {CustomerRoleType} from '../../../../../src/model/customer/customer-role-type';
import {Component, Input} from '@angular/core';
import {Contact} from '../../../../../src/model/customer/contact';
import {CustomerHubMock} from '../../../../mocks';

const headerText = 'Hakija';

@Component({
  selector: 'contact',
  template: ''
})
class ContactComponentMock {
  @Input() parentForm: FormGroup;
  @Input() customerId: number;
  @Input() customerRoleType: string;
  @Input() contactList: Array<Contact> = [];
  @Input() readonly: boolean;
  @Input() contactRequired = false;

  onCustomerRemove() {}
}

describe('CustomerComponent', () => {
  let comp: CustomerComponent;
  let fixture: ComponentFixture<CustomerComponent>;
  let page: CustomerPage;
  let parentForm: FormGroup;

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
    invoiceRecipientRadio: HTMLInputElement;

    addPageElements() {
      let debugElement = fixture.debugElement;
      this.cardTitle = debugElement.query(By.css('mat-card-title')).nativeElement;
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
      imports: [AlluCommonModule, ReactiveFormsModule, MatCardModule],
      declarations: [CustomerComponent, ContactComponentMock, CustomerInfoComponent],
      providers: [
        {provide: FormBuilder, useValue: new FormBuilder()},
        {provide: CustomerHub, useClass: CustomerHubMock}
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CustomerComponent);
    comp = fixture.componentInstance;
    const fb = new FormBuilder();
    parentForm = fb.group({});
    comp.parentForm = parentForm;
    comp.customerWithContacts = new CustomerWithContacts(CustomerRoleType.APPLICANT);
    comp.readonly = false;
    page = new CustomerPage();
    // fixture.detectChanges();
    page.addPageElements();
  });

  it('should show header text from input', fakeAsync(() => {
    fixture.detectChanges();
    expect(page.cardTitle.textContent).toContain(headerText);
  }));

  it('should fill the form with input customer', fakeAsync(() => {
    let customer = new Customer();
    customer.id = 1;
    customer.name = 'NameTest';
    customer.registryKey = '12345';
    customer.postalAddress.streetAddress = 'streetAddressTest';
    customer.postalAddress.postalCode = 'postalCodeTest';
    customer.postalAddress.city = 'cityTest';
    customer.phone = 'phoneTest';
    customer.email = 'emailTest';

    comp.customerWithContacts = new CustomerWithContacts(CustomerRoleType.APPLICANT, customer);
    comp.readonly = false;
    comp.ngOnInit();
    fixture.detectChanges();
    tick();
    expect(page.customerNameInput.value).toEqual(customer.name);
    expect(page.registryKeyInput.value).toEqual(customer.registryKey);
    expect(page.customerAddressInput.value).toEqual(customer.postalAddress.streetAddress);
    expect(page.customerPostalCodeInput.value).toEqual(customer.postalAddress.postalCode);
    expect(page.customerCityInput.value).toEqual(customer.postalAddress.city);
    expect(page.customerPhoneInput.value).toEqual(customer.phone);
    expect(page.customerEmailInput.value).toEqual(customer.email);
  }));

  it('should disable fields if readonly', () => {
    comp.readonly = true;
    fixture.detectChanges();
    expect(page.customerNameInput.disabled).toBeTruthy();
    expect(page.registryKeyInput.disabled).toBeTruthy();
    expect(page.customerAddressInput.disabled).toBeTruthy();
    expect(page.customerPostalCodeInput.disabled).toBeTruthy();
    expect(page.customerCityInput.disabled).toBeTruthy();
    expect(page.customerPhoneInput.disabled).toBeTruthy();
    expect(page.customerEmailInput.disabled).toBeTruthy();
  });
});
