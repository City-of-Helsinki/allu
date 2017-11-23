"use strict";
exports.__esModule = true;
var testing_1 = require("@angular/core/testing");
var platform_browser_1 = require("@angular/platform-browser");
var forms_1 = require("@angular/forms");
var recipients_by_type_component_1 = require("../../../../src/feature/admin/default-recipients/recipients-by-type.component");
var allu_common_module_1 = require("../../../../src/feature/common/allu-common.module");
var default_recipient_hub_1 = require("../../../../src/service/recipients/default-recipient-hub");
var application_type_1 = require("../../../../src/model/application/type/application-type");
var BehaviorSubject_1 = require("rxjs/BehaviorSubject");
var default_recipient_1 = require("../../../../src/model/common/default-recipient");
var default_recipient_mock_values_1 = require("../../../service/recipients/default-recipient-mock-values");
var http_response_1 = require("../../../../src/util/http-response");
var Observable_1 = require("rxjs/Observable");
var DefaultRecipientHubMock = (function () {
    function DefaultRecipientHubMock() {
        this.recipients$ = new BehaviorSubject_1.BehaviorSubject([]);
    }
    DefaultRecipientHubMock.prototype.defaultRecipientsByApplicationType = function (type) {
        return this.recipients$.asObservable().share();
    };
    DefaultRecipientHubMock.prototype.removeDefaultRecipient = function (id) { };
    DefaultRecipientHubMock.prototype.saveDefaultRecipient = function (recipient) { };
    return DefaultRecipientHubMock;
}());
describe('RecipientsByTypeComponent', function () {
    var fixture;
    var comp;
    var de;
    var hub;
    var page;
    var Page = (function () {
        function Page() {
        }
        Page.prototype.update = function () {
            this.addNewButton = de.query(platform_browser_1.By.css('th button')).nativeElement;
            this.rows = de.queryAll(platform_browser_1.By.css('tr'));
        };
        Page.prototype.getButtonFromRow = function (index, buttonIcon) {
            return page.rows[index].queryAll(platform_browser_1.By.css('button'))
                .filter(function (btn) { return btn.query(platform_browser_1.By.css('md-icon')).nativeElement.textContent === buttonIcon; })
                .map(function (btn) { return btn.nativeElement; })[0];
        };
        return Page;
    }());
    Page.emailDivSelector = platform_browser_1.By.css('.left-align');
    Page.emailInputSelector = platform_browser_1.By.css('input');
    function detectChangesAndUpdate() {
        fixture.detectChanges();
        testing_1.tick();
        page.update();
    }
    ;
    beforeEach(testing_1.async(function () {
        testing_1.TestBed.configureTestingModule({
            imports: [allu_common_module_1.AlluCommonModule, forms_1.ReactiveFormsModule],
            declarations: [
                recipients_by_type_component_1.RecipientsByTypeComponent
            ],
            providers: [
                forms_1.FormBuilder,
                { provide: default_recipient_hub_1.DefaultRecipientHub, useClass: DefaultRecipientHubMock }
            ]
        }).compileComponents();
    }));
    beforeEach(function () {
        fixture = testing_1.TestBed.createComponent(recipients_by_type_component_1.RecipientsByTypeComponent);
        comp = fixture.componentInstance;
        de = fixture.debugElement;
        comp.type = application_type_1.ApplicationType[application_type_1.ApplicationType.EVENT];
        hub = testing_1.TestBed.get(default_recipient_hub_1.DefaultRecipientHub);
        hub.recipients$.next([default_recipient_mock_values_1.RECIPIENT_ONE, default_recipient_mock_values_1.RECIPIENT_TWO]);
        comp.ngOnInit();
        fixture.detectChanges();
        page = new Page();
        page.update();
    });
    it('should show existing default recipients', testing_1.fakeAsync(function () {
        expect(page.rows.length).toEqual(2, 'Unexpected amount of comments');
        var firstRow = page.rows[0];
        expect(firstRow.query(Page.emailDivSelector).nativeElement.textContent).toEqual(default_recipient_mock_values_1.RECIPIENT_ONE.email);
    }));
    it('should add new on button press', testing_1.fakeAsync(function () {
        page.addNewButton.click();
        detectChangesAndUpdate();
        expect(page.rows.length).toEqual(3, 'Did not add a new row');
        expect(de.queryAll(Page.emailDivSelector).length).toBe(2, 'Existing rows with value not found');
        expect(de.queryAll(Page.emailInputSelector).length).toBe(1, 'Input not found');
        expect(page.rows[2].query(Page.emailInputSelector).nativeElement.value).toBeFalsy('Email input should not have value');
        comp.onItemCountChanged.subscribe(function (count) { return expect(count).toEqual(3, 'Component did not notify count by output'); });
    }));
    it('should delete row on delete button click', testing_1.fakeAsync(function () {
        spyOn(hub, 'removeDefaultRecipient').and.returnValue(Observable_1.Observable.of(new http_response_1.HttpResponse(http_response_1.HttpStatus.OK)));
        var deleteBtn = page.getButtonFromRow(0, 'clear');
        deleteBtn.click();
        detectChangesAndUpdate();
        expect(hub.removeDefaultRecipient).toHaveBeenCalledTimes(1);
        comp.onItemCountChanged.subscribe(function (count) { return expect(count).toEqual(2, 'Component did not notify count after removal'); });
    }));
    it('should toggle edit mode on edit button click', testing_1.fakeAsync(function () {
        var editBtn = page.getButtonFromRow(0, 'edit');
        editBtn.click();
        detectChangesAndUpdate();
        expect(de.queryAll(Page.emailInputSelector).length).toBe(1, 'Input not found');
    }));
    it('should save item on save button click', testing_1.fakeAsync(function () {
        var updated = new default_recipient_1.DefaultRecipient(default_recipient_mock_values_1.RECIPIENT_ONE.id, 'updated@email.fi', default_recipient_mock_values_1.RECIPIENT_ONE.applicationType);
        spyOn(hub, 'saveDefaultRecipient').and.returnValue(Observable_1.Observable.of(updated));
        var editBtn = page.getButtonFromRow(0, 'edit');
        editBtn.click();
        detectChangesAndUpdate();
        var firstRow = page.rows[0];
        var inputElement = firstRow.query(Page.emailInputSelector).nativeElement;
        inputElement.value = updated.email;
        inputElement.dispatchEvent(new Event('input'));
        detectChangesAndUpdate();
        var saveButton = page.getButtonFromRow(0, 'save');
        saveButton.click();
        expect(hub.saveDefaultRecipient).toHaveBeenCalledWith(updated);
    }));
    it('save button should be disabled on invalid email', testing_1.fakeAsync(function () {
        var editBtn = page.getButtonFromRow(0, 'edit');
        editBtn.click();
        detectChangesAndUpdate();
        var firstRow = page.rows[0];
        var inputElement = firstRow.query(Page.emailInputSelector).nativeElement;
        inputElement.value = 'invalidEmail';
        inputElement.dispatchEvent(new Event('input'));
        detectChangesAndUpdate();
        var saveButton = page.getButtonFromRow(0, 'save');
        expect(saveButton.disabled).toBeTruthy();
    }));
});
