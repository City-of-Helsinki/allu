import { ComponentFixture, fakeAsync, TestBed, tick, waitForAsync } from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {UntypedFormBuilder, UntypedFormGroup, ReactiveFormsModule} from '@angular/forms';
import {MatCardModule} from '@angular/material/card';

import {CustomerComponent} from '../../../../../app/feature/application/info/customer/customer.component';
import {AlluCommonModule} from '../../../../../app/feature/common/allu-common.module';
import {CustomerInfoComponent} from '../../../../../app/feature/customerregistry/customer/customer-info.component';
import {Customer} from '../../../../../app/model/customer/customer';
import {CustomerWithContacts} from '../../../../../app/model/customer/customer-with-contacts';
import {CustomerRoleType} from '../../../../../app/model/customer/customer-role-type';
import {Component, Input} from '@angular/core';
import {Contact} from '../../../../../app/model/customer/contact';
import {CurrentUserMock, CustomerServiceMock, NotificationServiceMock} from '../../../../mocks';
import {CustomerService} from '../../../../../app/service/customer/customer.service';
import {CodeSetService} from '../../../../../app/service/codeset/codeset.service';
import {CodeSet} from '../../../../../app/model/codeset/codeset';
import {Observable, of} from 'rxjs';
import {NotificationService} from '@feature/notification/notification.service';
import {CustomerOptionContentComponent} from '@feature/customerregistry/customer/customer-option-content.component';
import {RouterTestingModule} from '@angular/router/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { CurrentUser } from '@app/service/user/current-user';

const headerText = 'Hakija';

class CodeSetServiceMock {
  public getCountries(): Observable<Array<CodeSet>> {
    return of([{code: 'FI', type: 'Country', description: 'Suomi'}]);
  }
}

@Component({
  selector: 'contact',
  template: ''
})
class MockContactComponent {
  @Input() parentForm: UntypedFormGroup;
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
  let parentForm: UntypedFormGroup;

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
      const debugElement = fixture.debugElement;
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

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        AlluCommonModule,
        ReactiveFormsModule,
        MatCardModule,
        RouterTestingModule.withRoutes([]),
        NoopAnimationsModule
      ],
      declarations: [
        CustomerComponent,
        MockContactComponent,
        CustomerInfoComponent,
        CustomerOptionContentComponent
      ],
      providers: [
        {provide: UntypedFormBuilder, useValue: new UntypedFormBuilder()},
        {provide: CustomerService, useClass: CustomerServiceMock},
        {provide: CodeSetService, useClass: CodeSetServiceMock},
        {provide: NotificationService, useClass: NotificationServiceMock},
        {provide: CurrentUser, useClass: CurrentUserMock}
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CustomerComponent);
    comp = fixture.componentInstance;
    const fb = new UntypedFormBuilder();
    parentForm = fb.group({});

    comp.parentForm = parentForm;
    comp.readonly = false;
    comp.customerWithContacts = new CustomerWithContacts(CustomerRoleType.APPLICANT);
    page = new CustomerPage();
  });

  it('should show header text from input', fakeAsync(() => {
    fixture.detectChanges();
    page.addPageElements();
    console.log('page.cardTitle.textContent', page.cardTitle.textContent)
    expect(page.cardTitle.textContent).toContain(headerText);
  }));

  it('should fill the form with input customer', fakeAsync(() => {
    const customer = new Customer();
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
    fixture.detectChanges();
    tick();
    page.addPageElements();
    expect(page.customerNameInput.value).toEqual(customer.name);
    expect(page.registryKeyInput.value).toEqual(customer.registryKey);
    expect(page.customerAddressInput.value).toEqual(customer.postalAddress.streetAddress);
    expect(page.customerPostalCodeInput.value).toEqual(customer.postalAddress.postalCode);
    expect(page.customerCityInput.value).toEqual(customer.postalAddress.city);
    expect(page.customerPhoneInput.value).toEqual(customer.phone);
    expect(page.customerEmailInput.value).toEqual(customer.email);
  }));

  it('should disable fields if readonly', fakeAsync(() => {
    comp.readonly = true;
    comp.ngOnInit();
    expect(comp.customerForm.get('name').disabled).toBeTruthy('name should be disabled');
    expect(comp.customerForm.get('registryKey').disabled).toBeTruthy();
    expect(comp.customerForm.get('postalAddress').get('streetAddress').disabled).toBeTruthy();
    expect(comp.customerForm.get('postalAddress').get('postalCode').disabled).toBeTruthy();
    expect(comp.customerForm.get('postalAddress').get('city').disabled).toBeTruthy();
    expect(comp.customerForm.get('phone').disabled).toBeTruthy();
    expect(comp.customerForm.get('email').disabled).toBeTruthy();
    fixture.detectChanges();
  }));
});
