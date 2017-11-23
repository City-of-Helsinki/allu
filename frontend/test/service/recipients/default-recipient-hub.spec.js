"use strict";
exports.__esModule = true;
var default_recipient_hub_1 = require("../../../src/service/recipients/default-recipient-hub");
var testing_1 = require("@angular/core/testing");
var default_recipient_service_1 = require("../../../src/service/recipients/default-recipient.service");
var default_recipient_1 = require("../../../src/model/common/default-recipient");
var application_type_1 = require("../../../src/model/application/type/application-type");
var Observable_1 = require("rxjs/Observable");
var http_response_1 = require("../../../src/util/http-response");
var default_recipient_mock_values_1 = require("./default-recipient-mock-values");
var DefaultRecipientServiceMock = (function () {
    function DefaultRecipientServiceMock() {
    }
    DefaultRecipientServiceMock.prototype.getDefaultRecipients = function () {
        return Observable_1.Observable.of(default_recipient_mock_values_1.RECIPIENTS_ALL);
    };
    ;
    DefaultRecipientServiceMock.prototype.saveDefaultRecipient = function (recipient) { };
    ;
    DefaultRecipientServiceMock.prototype.removeDefaultRecipient = function (id) { };
    ;
    return DefaultRecipientServiceMock;
}());
;
describe('DefaultRecipientHub', function () {
    var hub;
    var defaultRecipientService;
    beforeEach(function () {
        var tb = testing_1.TestBed.configureTestingModule({
            providers: [
                { provide: default_recipient_service_1.DefaultRecipientService, useClass: DefaultRecipientServiceMock },
                default_recipient_hub_1.DefaultRecipientHub
            ]
        });
        hub = tb.get(default_recipient_hub_1.DefaultRecipientHub);
        defaultRecipientService = tb.get(default_recipient_service_1.DefaultRecipientService);
    });
    it('should emit values when initialized', testing_1.fakeAsync(function () {
        hub.defaultRecipients.subscribe(function (recipients) {
            expect(recipients.length).toEqual(2, 'Got unexpected number of recipients');
            expect(recipients[0]).toEqual(default_recipient_mock_values_1.RECIPIENT_ONE);
            expect(recipients[1]).toEqual(default_recipient_mock_values_1.RECIPIENT_TWO);
        });
    }));
    it('should emit new values on save', testing_1.fakeAsync(function () {
        spyOn(defaultRecipientService, 'getDefaultRecipients').and.returnValue(Observable_1.Observable.of([]));
        hub.loadDefaultRecipients();
        testing_1.tick();
        spyOn(defaultRecipientService, 'saveDefaultRecipient').and.returnValue(Observable_1.Observable.of(default_recipient_mock_values_1.RECIPIENT_ONE));
        hub.saveDefaultRecipient(default_recipient_mock_values_1.RECIPIENT_NEW).subscribe(function (recipient) {
            expect(recipient).toEqual(default_recipient_mock_values_1.RECIPIENT_ONE);
        });
        testing_1.tick();
        hub.defaultRecipients.last().subscribe(function (recipients) {
            expect(recipients.length).toEqual(1, 'Got unexpected number of recipients');
            expect(recipients[0]).toEqual(default_recipient_mock_values_1.RECIPIENT_ONE);
        });
    }));
    it('should update and emit new values', testing_1.fakeAsync(function () {
        spyOn(defaultRecipientService, 'getDefaultRecipients').and.returnValue(Observable_1.Observable.of(default_recipient_mock_values_1.RECIPIENTS_ALL));
        hub.loadDefaultRecipients();
        testing_1.tick();
        var updatedRecipient = new default_recipient_1.DefaultRecipient(default_recipient_mock_values_1.RECIPIENT_ONE.id, 'newEmail', application_type_1.ApplicationType[application_type_1.ApplicationType.SHORT_TERM_RENTAL]);
        spyOn(defaultRecipientService, 'saveDefaultRecipient').and.returnValue(Observable_1.Observable.of(updatedRecipient));
        hub.saveDefaultRecipient(updatedRecipient).subscribe(function (recipient) { return expect(recipient).toEqual(updatedRecipient); });
        testing_1.tick();
        hub.defaultRecipients.last().subscribe(function (recipients) {
            expect(recipients.length).toEqual(2, 'Length should be same as before');
            expect(recipients.find(function (r) { return r.id === updatedRecipient.id; })).toEqual(updatedRecipient);
        });
    }));
    it('should remove and emit new values', testing_1.fakeAsync(function () {
        spyOn(defaultRecipientService, 'getDefaultRecipients').and.returnValue(Observable_1.Observable.of(default_recipient_mock_values_1.RECIPIENTS_ALL));
        hub.loadDefaultRecipients();
        testing_1.tick();
        spyOn(defaultRecipientService, 'removeDefaultRecipient').and.returnValue(Observable_1.Observable.of(new http_response_1.HttpResponse(http_response_1.HttpStatus.OK)));
        hub.removeDefaultRecipient(default_recipient_mock_values_1.RECIPIENT_ONE.id).subscribe(function (response) { return expect(response.status).toEqual(http_response_1.HttpStatus.OK); });
        testing_1.tick();
        hub.defaultRecipients.last().subscribe(function (recipients) {
            expect(recipients.length).toEqual(1, 'Recipient was not deleted');
            expect(recipients.find(function (r) { return r.id === default_recipient_mock_values_1.RECIPIENT_ONE.id; })).not.toBeDefined();
            expect(recipients.find(function (r) { return r.id === default_recipient_mock_values_1.RECIPIENT_TWO.id; })).toBeDefined();
        });
    }));
});
