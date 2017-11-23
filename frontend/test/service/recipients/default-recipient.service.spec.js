"use strict";
exports.__esModule = true;
var testing_1 = require("@angular/core/testing");
var angular2_jwt_1 = require("angular2-jwt");
var http_1 = require("@angular/http");
var testing_2 = require("@angular/http/testing");
var default_recipient_1 = require("../../../src/model/common/default-recipient");
var default_recipient_service_1 = require("../../../src/service/recipients/default-recipient.service");
var error_handler_service_1 = require("../../../src/service/error/error-handler.service");
var http_response_1 = require("../../../src/util/http-response");
var default_recipient_mock_values_1 = require("./default-recipient-mock-values");
var ERROR_RESPONSE = new http_1.Response(new http_1.ResponseOptions({
    type: http_1.ResponseType.Error,
    status: 404
}));
var ErrorHandlerMock = (function () {
    function ErrorHandlerMock() {
    }
    ErrorHandlerMock.prototype.handle = function (error, message) { };
    return ErrorHandlerMock;
}());
;
describe('DefaultRecipientService', function () {
    var service;
    var backend;
    var errorHandler;
    var lastConnection;
    var authHttp;
    beforeEach(function () {
        var tb = testing_1.TestBed.configureTestingModule({
            imports: [http_1.HttpModule],
            providers: [
                testing_2.MockBackend,
                http_1.BaseRequestOptions,
                { provide: http_1.ConnectionBackend, useClass: testing_2.MockBackend },
                { provide: http_1.RequestOptions, useClass: http_1.BaseRequestOptions },
                http_1.Http,
                { provide: angular2_jwt_1.AuthHttp, useExisting: http_1.Http, deps: [http_1.Http] },
                { provide: error_handler_service_1.ErrorHandler, useClass: ErrorHandlerMock },
                default_recipient_service_1.DefaultRecipientService
            ]
        });
        service = tb.get(default_recipient_service_1.DefaultRecipientService);
        backend = tb.get(http_1.ConnectionBackend);
        backend.connections.subscribe(function (connection) { return lastConnection = connection; });
        errorHandler = tb.get(error_handler_service_1.ErrorHandler);
        authHttp = tb.get(angular2_jwt_1.AuthHttp);
    });
    it('getComments() should return queried comments', testing_1.fakeAsync(function () {
        var result;
        service.getDefaultRecipients().subscribe(function (r) { return result = r; });
        lastConnection.mockRespond(new http_1.Response(new http_1.ResponseOptions({
            body: JSON.stringify([default_recipient_mock_values_1.RECIPIENT_ONE, default_recipient_mock_values_1.RECIPIENT_TWO])
        })));
        testing_1.tick();
        expect(result[0]).toEqual(default_recipient_mock_values_1.RECIPIENT_ONE, ' RECIPIENT_ONE should be the first recipient');
        expect(result[1]).toEqual(default_recipient_mock_values_1.RECIPIENT_TWO, ' RECIPIENT_TWO should be the second recipient');
    }));
    it('getComments() should handle errors', testing_1.fakeAsync(function () {
        var result;
        spyOn(errorHandler, 'handle');
        service.getDefaultRecipients().subscribe(function (r) { return result = r; }, function (error) { });
        lastConnection.mockError(ERROR_RESPONSE);
        testing_1.tick();
        expect(result).toBeUndefined();
        expect(errorHandler.handle).toHaveBeenCalledTimes(1);
    }));
    it('save() recipient without id should create new', testing_1.fakeAsync(function () {
        var result;
        var updatedRecipient = new default_recipient_1.DefaultRecipient(default_recipient_mock_values_1.RECIPIENT_ONE.id, default_recipient_mock_values_1.RECIPIENT_ONE.email, default_recipient_mock_values_1.RECIPIENT_ONE.applicationType);
        updatedRecipient.id = 10;
        service.saveDefaultRecipient(default_recipient_mock_values_1.RECIPIENT_NEW).subscribe(function (r) { return result = r; });
        lastConnection.mockRespond(new http_1.Response(new http_1.ResponseOptions({
            body: JSON.stringify(updatedRecipient)
        })));
        testing_1.tick();
        expect(lastConnection.request.method).toEqual(http_1.RequestMethod.Post);
        expect(result).toEqual(updatedRecipient, 'Recipient was not saved');
    }));
    it('save() recipient with id should update', testing_1.fakeAsync(function () {
        var result;
        service.saveDefaultRecipient(default_recipient_mock_values_1.RECIPIENT_ONE).subscribe(function (r) { return result = r; });
        lastConnection.mockRespond(new http_1.Response(new http_1.ResponseOptions({
            body: JSON.stringify(default_recipient_mock_values_1.RECIPIENT_ONE)
        })));
        testing_1.tick();
        expect(lastConnection.request.method).toEqual(http_1.RequestMethod.Put);
        expect(result).toEqual(default_recipient_mock_values_1.RECIPIENT_ONE, 'RECIPIENT was not saved');
    }));
    it('save() comment should handle errors', testing_1.fakeAsync(function () {
        var result;
        spyOn(errorHandler, 'handle');
        service.saveDefaultRecipient(default_recipient_mock_values_1.RECIPIENT_ONE).subscribe(function (r) { return result = r; }, function (error) { });
        lastConnection.mockError(ERROR_RESPONSE);
        testing_1.tick();
        expect(result).toBeUndefined();
        expect(errorHandler.handle).toHaveBeenCalledTimes(1);
    }));
    it('remove() should remove comment with matching id', testing_1.fakeAsync(function () {
        var result;
        service.removeDefaultRecipient(default_recipient_mock_values_1.RECIPIENT_ONE.id).subscribe(function (r) { return result = r; });
        lastConnection.mockRespond(new http_1.Response(new http_1.ResponseOptions({
            status: 200
        })));
        testing_1.tick();
        expect(lastConnection.request.method).toEqual(http_1.RequestMethod.Delete);
        expect(result.status).toEqual(http_response_1.HttpStatus.OK);
    }));
    it('remove() comment should handle errors', testing_1.fakeAsync(function () {
        var result;
        spyOn(errorHandler, 'handle');
        service.removeDefaultRecipient(default_recipient_mock_values_1.RECIPIENT_ONE.id).subscribe(function (r) { return result = r; }, function (error) { });
        lastConnection.mockError(ERROR_RESPONSE);
        testing_1.tick();
        expect(result).toBeUndefined();
        expect(errorHandler.handle).toHaveBeenCalledTimes(1);
    }));
    it('remove() should do nothing when no id is passed', testing_1.fakeAsync(function () {
        var result;
        spyOn(authHttp, 'delete');
        service.removeDefaultRecipient(undefined).subscribe(function (r) { return result = r; });
        testing_1.tick();
        expect(result.status).toEqual(http_response_1.HttpStatus.OK);
        expect(authHttp["delete"]).not.toHaveBeenCalled();
    }));
});
