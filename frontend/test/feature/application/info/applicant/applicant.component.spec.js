"use strict";
var testing_1 = require("@angular/core/testing");
var platform_browser_1 = require("@angular/platform-browser");
var forms_1 = require("@angular/forms");
var material_1 = require("@angular/material");
var applicant_component_1 = require("../../../../../src/feature/application/info/applicant/applicant.component");
var applicant_1 = require("../../../../../src/model/application/applicant");
var allu_common_module_1 = require("../../../../../src/feature/common/allu-common.module");
var headerText = 'HeaderTextTest';
var formName = 'FormNameTest';
describe('ApplicantComponent', function () {
    var comp;
    var fixture;
    var page;
    var ApplicantPage = (function () {
        function ApplicantPage() {
        }
        ApplicantPage.prototype.addPageElements = function () {
            var debugElement = fixture.debugElement;
            this.cardTitle = debugElement.query(platform_browser_1.By.css('md-card-title')).nativeElement;
            this.countryInput = debugElement.query(platform_browser_1.By.css('[formControlName="country"]')).nativeElement;
            this.applicantNameInput = debugElement.query(platform_browser_1.By.css('[formControlName="name"]')).nativeElement;
            this.registryKeyInput = debugElement.query(platform_browser_1.By.css('[formControlName="registryKey"]')).nativeElement;
            this.applicantAddressInput = debugElement.query(platform_browser_1.By.css('[formControlName="streetAddress"]')).nativeElement;
            this.applicantPostalCodeInput = debugElement.query(platform_browser_1.By.css('[formControlName="postalCode"]')).nativeElement;
            this.applicantCityInput = debugElement.query(platform_browser_1.By.css('[formControlName="city"]')).nativeElement;
            this.applicantPhoneInput = debugElement.query(platform_browser_1.By.css('[formControlName="phone"]')).nativeElement;
            this.applicantEmailInput = debugElement.query(platform_browser_1.By.css('[formControlName="email"]')).nativeElement;
        };
        return ApplicantPage;
    }());
    beforeEach(testing_1.async(function () {
        testing_1.TestBed.configureTestingModule({
            imports: [allu_common_module_1.AlluCommonModule, forms_1.ReactiveFormsModule, material_1.MdCardModule],
            declarations: [applicant_component_1.ApplicantComponent],
            providers: [
                { provide: forms_1.FormBuilder, useValue: new forms_1.FormBuilder() }
            ]
        }).compileComponents();
    }));
    beforeEach(function () {
        fixture = testing_1.TestBed.createComponent(applicant_component_1.ApplicantComponent);
        comp = fixture.componentInstance;
        comp.applicationForm = new forms_1.FormGroup({});
        comp.applicant = new applicant_1.Applicant();
        comp.readonly = false;
        comp.headerText = headerText;
        comp.formName = formName;
        page = new ApplicantPage();
        page.addPageElements();
    });
    it('should show header text from input', function () {
        fixture.detectChanges();
        fixture.whenStable().then(function (result) {
            expect(page.cardTitle.textContent).toContain(headerText);
        });
    });
    it('should fill the form with input applicant', function () {
        var applicant = new applicant_1.Applicant();
        applicant.name = 'NameTest';
        applicant.registryKey = '12345';
        applicant.postalAddress.streetAddress = 'streetAddressTest';
        applicant.postalAddress.postalCode = 'postalCodeTest';
        applicant.postalAddress.city = 'cityTest';
        applicant.phone = 'phoneTest';
        applicant.email = 'emailTest';
        comp.applicant = applicant;
        fixture.detectChanges();
        fixture.whenStable().then(function (result) {
            expect(page.applicantNameInput.value).toEqual(applicant.name);
            expect(page.registryKeyInput.value).toEqual(applicant.registryKey);
            expect(page.applicantAddressInput.value).toEqual(applicant.postalAddress.streetAddress);
            expect(page.applicantPostalCodeInput.value).toEqual(applicant.postalAddress.postalCode);
            expect(page.applicantCityInput.value).toEqual(applicant.postalAddress.city);
            expect(page.applicantPhoneInput.value).toEqual(applicant.phone);
            expect(page.applicantEmailInput.value).toEqual(applicant.email);
        });
    });
    it('should disable fields if readonly', function () {
        comp.readonly = true;
        fixture.detectChanges();
        fixture.whenStable().then(function (result) {
            expect(page.applicantNameInput.disabled).toBeTruthy();
            expect(page.registryKeyInput.disabled).toBeTruthy();
            expect(page.applicantAddressInput.disabled).toBeTruthy();
            expect(page.applicantPostalCodeInput.disabled).toBeTruthy();
            expect(page.applicantCityInput.disabled).toBeTruthy();
            expect(page.applicantPhoneInput.disabled).toBeTruthy();
            expect(page.applicantEmailInput.disabled).toBeTruthy();
        });
    });
});
