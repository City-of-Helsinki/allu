import {ComponentFixture, TestBed, async, fakeAsync} from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {FormGroup, FormBuilder, ReactiveFormsModule} from '@angular/forms';
import {MdCardModule} from '@angular/material';

import {ApplicantComponent} from '../../../../../src/feature/application/info/applicant/applicant.component';
import {Applicant} from '../../../../../src/model/application/applicant';
import {AlluCommonModule} from '../../../../../src/feature/common/allu-common.module';
import {CustomerHub} from '../../../../../src/service/customer/customer-hub';

const headerText = 'HeaderTextTest';
const formName = 'FormNameTest';

class ApplicantHubMock {
  searchApplicantsByField(fieldName: string, term: string) {}
}

describe('ApplicantComponent', () => {
  let comp: ApplicantComponent;
  let fixture: ComponentFixture<ApplicantComponent>;
  let page: ApplicantPage;

  class ApplicantPage {
    cardTitle: HTMLElement;
    countryInput: HTMLInputElement;
    applicantNameInput: HTMLInputElement;
    registryKeyInput: HTMLInputElement;
    applicantAddressInput: HTMLInputElement;
    applicantPostalCodeInput: HTMLInputElement;
    applicantCityInput: HTMLInputElement;
    applicantPhoneInput: HTMLInputElement;
    applicantEmailInput: HTMLInputElement;

    addPageElements() {
      let debugElement = fixture.debugElement;
      this.cardTitle = debugElement.query(By.css('md-card-title')).nativeElement;
      this.countryInput = debugElement.query(By.css('[formControlName="country"]')).nativeElement;
      this.applicantNameInput = debugElement.query(By.css('[formControlName="name"]')).nativeElement;
      this.registryKeyInput = debugElement.query(By.css('[formControlName="registryKey"]')).nativeElement;
      this.applicantAddressInput = debugElement.query(By.css('[formControlName="streetAddress"]')).nativeElement;
      this.applicantPostalCodeInput = debugElement.query(By.css('[formControlName="postalCode"]')).nativeElement;
      this.applicantCityInput = debugElement.query(By.css('[formControlName="city"]')).nativeElement;
      this.applicantPhoneInput = debugElement.query(By.css('[formControlName="phone"]')).nativeElement;
      this.applicantEmailInput = debugElement.query(By.css('[formControlName="email"]')).nativeElement;
    }
  }

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [AlluCommonModule, ReactiveFormsModule, MdCardModule],
      declarations: [ApplicantComponent],
      providers: [
        {provide: FormBuilder, useValue: new FormBuilder()},
        {provide: CustomerHub, useClass: ApplicantHubMock}
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ApplicantComponent);
    comp = fixture.componentInstance;
    comp.applicationForm = new FormGroup({});
    comp.applicant = new Applicant();
    comp.readonly = false;
    comp.headerText = headerText;
    comp.formName = formName;
    page = new ApplicantPage();
    page.addPageElements();
  });

  it('should show header text from input', () => {
    fixture.detectChanges();
    fixture.whenStable().then(result => {
      expect(page.cardTitle.textContent).toContain(headerText);
    });
  });

  it('should fill the form with input applicant', () => {
    let applicant = new Applicant();
    applicant.name = 'NameTest';
    applicant.registryKey = '12345';
    applicant.postalAddress.streetAddress = 'streetAddressTest';
    applicant.postalAddress.postalCode = 'postalCodeTest';
    applicant.postalAddress.city = 'cityTest';
    applicant.phone = 'phoneTest';
    applicant.email = 'emailTest';

    comp.applicant = applicant;
    fixture.detectChanges();
    fixture.whenStable().then(result => {
      expect(page.applicantNameInput.value).toEqual(applicant.name);
      expect(page.registryKeyInput.value).toEqual(applicant.registryKey);
      expect(page.applicantAddressInput.value).toEqual(applicant.postalAddress.streetAddress);
      expect(page.applicantPostalCodeInput.value).toEqual(applicant.postalAddress.postalCode);
      expect(page.applicantCityInput.value).toEqual(applicant.postalAddress.city);
      expect(page.applicantPhoneInput.value).toEqual(applicant.phone);
      expect(page.applicantEmailInput.value).toEqual(applicant.email);
    });
  });

  it('should disable fields if readonly', () => {
    comp.readonly = true;
    fixture.detectChanges();
    fixture.whenStable().then(result => {
      expect(page.applicantNameInput.disabled).toBeTruthy();
      expect(page.registryKeyInput.disabled).toBeTruthy();
      expect(page.applicantAddressInput.disabled).toBeTruthy();
      expect(page.applicantPostalCodeInput.disabled).toBeTruthy();
      expect(page.applicantCityInput.disabled).toBeTruthy();
      expect(page.applicantPhoneInput.disabled).toBeTruthy();
      expect(page.applicantEmailInput.disabled).toBeTruthy();
    });
  });

  it('should fill fields from autocomplete', () => {
    let applicant = new Applicant();
    applicant.name = 'NameTest';
    applicant.registryKey = '12345';
    applicant.postalAddress.streetAddress = 'streetAddressTest';
    applicant.postalAddress.postalCode = 'postalCodeTest';
    applicant.postalAddress.city = 'cityTest';
    applicant.phone = 'phoneTest';
    applicant.email = 'emailTest';

    let nameElement = fixture.debugElement.query(By.css('[formControlName="name"]'));
    nameElement.triggerEventHandler('onSelection', applicant);
    fixture.detectChanges();
    fixture.whenStable().then(result => {
      // TODO: fix the test to check field values
    });
  });
});
