"use strict";
exports.__esModule = true;
var testing_1 = require("@angular/core/testing");
var platform_browser_1 = require("@angular/platform-browser");
var forms_1 = require("@angular/forms");
var material_1 = require("@angular/material");
var contact_component_1 = require("../../../../../src/feature/application/info/contact/contact.component");
var allu_common_module_1 = require("../../../../../src/feature/common/allu-common.module");
var application_state_1 = require("../../../../../src/service/application/application-state");
var mocks_1 = require("../../../../mocks");
var customer_hub_1 = require("../../../../../src/service/customer/customer-hub");
var customer_role_type_1 = require("../../../../../src/model/customer/customer-role-type");
var customer_with_contacts_form_1 = require("../../../../../src/feature/customerregistry/customer/customer-with-contacts.form");
var contact_1 = require("../../../../../src/model/customer/contact");
var selector_helpers_1 = require("../../../../selector-helpers");
var application_type_1 = require("../../../../../src/model/application/type/application-type");
var application_1 = require("../../../../../src/model/application/application");
var cable_report_form_1 = require("../../../../../src/feature/application/info/cable-report/cable-report.form");
var CONTACT1 = new contact_1.Contact(1, 1, 'contact1', 'address1');
var CONTACT2 = new contact_1.Contact(2, 1, 'contact2', 'address2');
var CONTACTS_ALL = [CONTACT1, CONTACT2];
describe('ContactComponent', function () {
    var comp;
    var de;
    var fixture;
    var page;
    var parentForm;
    var applicationStateMock;
    var customerHubMock;
    var ContactPage = (function () {
        function ContactPage() {
            this.update();
        }
        ContactPage.prototype.getFromContact = function (index, selector) {
            return this.contacts[index].query(platform_browser_1.By.css(selector));
        };
        ContactPage.prototype.update = function () {
            this.contacts = de.queryAll(platform_browser_1.By.css('.contact-card'));
        };
        return ContactPage;
    }());
    function detectChangesAndUpdate() {
        fixture.detectChanges();
        testing_1.tick();
        page.update();
    }
    ;
    beforeEach(testing_1.async(function () {
        testing_1.TestBed.configureTestingModule({
            imports: [allu_common_module_1.AlluCommonModule, forms_1.ReactiveFormsModule, material_1.MdCardModule],
            declarations: [contact_component_1.ContactComponent],
            providers: [
                { provide: application_state_1.ApplicationState, useClass: mocks_1.ApplicationStateMock },
                { provide: forms_1.FormBuilder, useValue: new forms_1.FormBuilder() },
                { provide: customer_hub_1.CustomerHub, useClass: mocks_1.CustomerHubMock }
            ]
        }).compileComponents();
        applicationStateMock = testing_1.TestBed.get(application_state_1.ApplicationState);
        customerHubMock = testing_1.TestBed.get(customer_hub_1.CustomerHub);
    }));
    beforeEach(function () {
        parentForm = createParentForm();
        fixture = testing_1.TestBed.createComponent(contact_component_1.ContactComponent);
        de = fixture.debugElement;
        comp = fixture.componentInstance;
        comp.parentForm = parentForm;
        comp.readonly = false;
        comp.customerRoleType = customer_role_type_1.CustomerRoleType[customer_role_type_1.CustomerRoleType.APPLICANT];
        comp.contactList = CONTACTS_ALL;
        fixture.detectChanges();
        page = new ContactPage();
    });
    it('should show contacts from input', function () {
        fixture.whenStable().then(function (result) {
            expect(page.contacts.length).toEqual(CONTACTS_ALL.length);
            expect(page.getFromContact(0, '[formControlName="name"]').nativeElement.value).toEqual(CONTACT1.name);
            expect(page.getFromContact(0, '[formControlName="streetAddress"]').nativeElement.value).toEqual(CONTACT1.streetAddress);
            expect(page.getFromContact(1, '[formControlName="name"]').nativeElement.value).toEqual(CONTACT2.name);
            expect(page.getFromContact(1, '[formControlName="streetAddress"]').nativeElement.value).toEqual(CONTACT2.streetAddress);
        });
    });
    it('should show contact info fields disabled', testing_1.fakeAsync(function () {
        fixture.whenStable().then(function (result) {
            expect(page.getFromContact(0, '[formControlName="name"] [disabled]')).toBeDefined();
        });
    }));
    it('should add contact when add is called', testing_1.fakeAsync(function () {
        comp.addContact();
        detectChangesAndUpdate();
        expect(page.contacts.length).toEqual(CONTACTS_ALL.length + 1);
    }));
    it('should remove contact when remove is clicked', testing_1.fakeAsync(function () {
        var removeBtn = selector_helpers_1.getMdIconButton(page.contacts[0], 'clear');
        removeBtn.click();
        detectChangesAndUpdate();
        expect(page.contacts.length).toEqual(CONTACTS_ALL.length - 1);
    }));
    it('should not allow to remove last contact when contact is required', testing_1.fakeAsync(function () {
        comp.contactRequired = true;
        selector_helpers_1.getMdIconButton(page.contacts[1], 'clear').click();
        detectChangesAndUpdate();
        expect(page.contacts.length).toEqual(1);
        // Last one can't be removed
        expect(selector_helpers_1.getMdIconButton(page.contacts[0], 'clear')).toBeUndefined();
    }));
    it('should show edit button when application is in edit mode', testing_1.fakeAsync(function () {
        comp.readonly = false;
        detectChangesAndUpdate();
        var editBtn = selector_helpers_1.getMdIconButton(page.contacts[0], 'mode_edit');
        expect(editBtn).toBeDefined();
    }));
    it('should hide edit button when application is in summary mode', testing_1.fakeAsync(function () {
        comp.readonly = true;
        detectChangesAndUpdate();
        var editBtn = selector_helpers_1.getMdIconButton(page.contacts[0], 'mode_edit');
        expect(editBtn).toBeUndefined();
    }));
    it('should clear other fields when name is edited', testing_1.fakeAsync(function () {
        comp.readonly = true;
        detectChangesAndUpdate();
        var inputElement = page.getFromContact(0, '[formControlName="name"]').nativeElement;
        inputElement.value = 'updated value';
        inputElement.dispatchEvent(new Event('keyup'));
        detectChangesAndUpdate();
        expect(page.getFromContact(0, '[formControlName="streetAddress"]').nativeElement.value).toBe('');
    }));
    it('should show and select first contact from applicant as an orderer for cable report', testing_1.fakeAsync(function () {
        reInitWithCableReport();
        expect(page.getFromContact(0, '.mat-radio-button')).toBeTruthy();
        expect(page.getFromContact(0, '.mat-radio-button').componentInstance.checked).toBe(true);
    }));
    it('should uncheck orderer when other orderer is selected', testing_1.fakeAsync(function () {
        reInitWithCableReport();
        page.getFromContact(1, '.mat-radio-label').nativeElement.click();
        detectChangesAndUpdate();
        expect(page.getFromContact(0, '.mat-radio-button').componentInstance.checked).toBe(false, 'original checkbox was checked');
        expect(page.getFromContact(1, '.mat-radio-button').componentInstance.checked).toBe(true, 'clicked checkbox was unchecked');
    }));
    function reInitWithCableReport() {
        var app = new application_1.Application();
        app.type = application_type_1.ApplicationType[application_type_1.ApplicationType.CABLE_REPORT];
        spyOnProperty(applicationStateMock, 'application', 'get').and.returnValue(app);
        while (comp.contacts.length) {
            comp.contacts.removeAt(0);
        }
        comp.ngOnInit();
        detectChangesAndUpdate();
    }
    function createParentForm() {
        var fb = new forms_1.FormBuilder();
        var form = fb.group({});
        form.addControl(customer_with_contacts_form_1.CustomerWithContactsForm.formName(customer_role_type_1.CustomerRoleType.APPLICANT), customer_with_contacts_form_1.CustomerWithContactsForm.initialForm(fb, customer_role_type_1.CustomerRoleType.APPLICANT));
        form.addControl('ordererId', fb.group(cable_report_form_1.OrdererIdForm.createDefault()));
        return form;
    }
});
