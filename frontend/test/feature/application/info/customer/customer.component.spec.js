"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
exports.__esModule = true;
var testing_1 = require("@angular/core/testing");
var platform_browser_1 = require("@angular/platform-browser");
var forms_1 = require("@angular/forms");
var material_1 = require("@angular/material");
var customer_component_1 = require("../../../../../src/feature/application/info/customer/customer.component");
var allu_common_module_1 = require("../../../../../src/feature/common/allu-common.module");
var customer_hub_1 = require("../../../../../src/service/customer/customer-hub");
var customer_info_component_1 = require("../../../../../src/feature/customerregistry/customer/customer-info.component");
var customer_1 = require("../../../../../src/model/customer/customer");
var customer_with_contacts_1 = require("../../../../../src/model/customer/customer-with-contacts");
var customer_role_type_1 = require("../../../../../src/model/customer/customer-role-type");
var core_1 = require("@angular/core");
var mocks_1 = require("../../../../mocks");
var headerText = 'Hakija';
var ContactComponentMock = (function () {
    function ContactComponentMock() {
        this.contactList = [];
        this.contactRequired = false;
    }
    ContactComponentMock.prototype.onCustomerRemove = function () { };
    return ContactComponentMock;
}());
__decorate([
    core_1.Input()
], ContactComponentMock.prototype, "parentForm");
__decorate([
    core_1.Input()
], ContactComponentMock.prototype, "customerId");
__decorate([
    core_1.Input()
], ContactComponentMock.prototype, "customerRoleType");
__decorate([
    core_1.Input()
], ContactComponentMock.prototype, "contactList");
__decorate([
    core_1.Input()
], ContactComponentMock.prototype, "readonly");
__decorate([
    core_1.Input()
], ContactComponentMock.prototype, "contactRequired");
ContactComponentMock = __decorate([
    core_1.Component({
        selector: 'contact',
        template: ''
    })
], ContactComponentMock);
describe('CustomerComponent', function () {
    var comp;
    var fixture;
    var page;
    var parentForm;
    var CustomerPage = (function () {
        function CustomerPage() {
        }
        CustomerPage.prototype.addPageElements = function () {
            var debugElement = fixture.debugElement;
            this.cardTitle = debugElement.query(platform_browser_1.By.css('md-card-title')).nativeElement;
            this.countryInput = debugElement.query(platform_browser_1.By.css('[formControlName="country"]')).nativeElement;
            this.customerNameInput = debugElement.query(platform_browser_1.By.css('[formControlName="name"]')).nativeElement;
            this.registryKeyInput = debugElement.query(platform_browser_1.By.css('[formControlName="registryKey"]')).nativeElement;
            this.customerAddressInput = debugElement.query(platform_browser_1.By.css('[formControlName="streetAddress"]')).nativeElement;
            this.customerPostalCodeInput = debugElement.query(platform_browser_1.By.css('[formControlName="postalCode"]')).nativeElement;
            this.customerCityInput = debugElement.query(platform_browser_1.By.css('[formControlName="city"]')).nativeElement;
            this.customerPhoneInput = debugElement.query(platform_browser_1.By.css('[formControlName="phone"]')).nativeElement;
            this.customerEmailInput = debugElement.query(platform_browser_1.By.css('[formControlName="email"]')).nativeElement;
        };
        return CustomerPage;
    }());
    beforeEach(testing_1.async(function () {
        testing_1.TestBed.configureTestingModule({
            imports: [allu_common_module_1.AlluCommonModule, forms_1.ReactiveFormsModule, material_1.MdCardModule],
            declarations: [customer_component_1.CustomerComponent, ContactComponentMock, customer_info_component_1.CustomerInfoComponent],
            providers: [
                { provide: forms_1.FormBuilder, useValue: new forms_1.FormBuilder() },
                { provide: customer_hub_1.CustomerHub, useClass: mocks_1.CustomerHubMock }
            ]
        }).compileComponents();
    }));
    beforeEach(function () {
        fixture = testing_1.TestBed.createComponent(customer_component_1.CustomerComponent);
        comp = fixture.componentInstance;
        var fb = new forms_1.FormBuilder();
        parentForm = fb.group({});
        comp.parentForm = parentForm;
        comp.customerWithContacts = new customer_with_contacts_1.CustomerWithContacts(customer_role_type_1.CustomerRoleType.APPLICANT);
        comp.readonly = false;
        page = new CustomerPage();
        // fixture.detectChanges();
        page.addPageElements();
    });
    it('should show header text from input', testing_1.fakeAsync(function () {
        fixture.detectChanges();
        expect(page.cardTitle.textContent).toContain(headerText);
    }));
    it('should fill the form with input customer', testing_1.fakeAsync(function () {
        var customer = new customer_1.Customer();
        customer.id = 1;
        customer.name = 'NameTest';
        customer.registryKey = '12345';
        customer.postalAddress.streetAddress = 'streetAddressTest';
        customer.postalAddress.postalCode = 'postalCodeTest';
        customer.postalAddress.city = 'cityTest';
        customer.phone = 'phoneTest';
        customer.email = 'emailTest';
        comp.customerWithContacts = new customer_with_contacts_1.CustomerWithContacts(customer_role_type_1.CustomerRoleType.APPLICANT, customer);
        comp.readonly = false;
        comp.ngOnInit();
        fixture.detectChanges();
        testing_1.tick();
        expect(page.customerNameInput.value).toEqual(customer.name);
        expect(page.registryKeyInput.value).toEqual(customer.registryKey);
        expect(page.customerAddressInput.value).toEqual(customer.postalAddress.streetAddress);
        expect(page.customerPostalCodeInput.value).toEqual(customer.postalAddress.postalCode);
        expect(page.customerCityInput.value).toEqual(customer.postalAddress.city);
        expect(page.customerPhoneInput.value).toEqual(customer.phone);
        expect(page.customerEmailInput.value).toEqual(customer.email);
    }));
    it('should disable fields if readonly', function () {
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
