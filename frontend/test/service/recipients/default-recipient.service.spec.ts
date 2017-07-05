import {fakeAsync, TestBed, tick} from '@angular/core/testing';
import {AuthHttp} from 'angular2-jwt';
import {
  BaseRequestOptions,
  ConnectionBackend,
  Http,
  HttpModule,
  RequestMethod,
  RequestOptions,
  Response,
  ResponseOptions,
  ResponseType
} from '@angular/http';
import {MockBackend} from '@angular/http/testing';
import {DefaultRecipient} from '../../../src/model/common/default-recipient';
import {DefaultRecipientService} from '../../../src/service/recipients/default-recipient.service';
import {ErrorHandler} from '../../../src/service/error/error-handler.service';
import {HttpResponse, HttpStatus} from '../../../src/util/http-response';
import createSpy = jasmine.createSpy;
import {RECIPIENT_NEW, RECIPIENT_ONE, RECIPIENT_TWO} from './default-recipient-mock-values';

const ERROR_RESPONSE = new Response(new ResponseOptions({
  type: ResponseType.Error,
  status: 404
}));

class ErrorHandlerMock {
  handle(error: any, message?: string) {}
};

describe('DefaultRecipientService', () => {
  let service: DefaultRecipientService;
  let backend: MockBackend;
  let errorHandler: ErrorHandlerMock;
  let lastConnection: any;
  let authHttp: AuthHttp;

  beforeEach(() => {
    const tb = TestBed.configureTestingModule({
      imports: [HttpModule],
      providers: [
        MockBackend,
        BaseRequestOptions,
        { provide: ConnectionBackend, useClass: MockBackend },
        { provide: RequestOptions, useClass: BaseRequestOptions },
        Http,
        { provide: AuthHttp, useExisting: Http, deps: [Http] },
        { provide: ErrorHandler, useClass: ErrorHandlerMock},
        DefaultRecipientService
      ]
    });
    service = tb.get(DefaultRecipientService);
    backend = tb.get(ConnectionBackend) as MockBackend;
    backend.connections.subscribe((connection: any) => lastConnection = connection);
    errorHandler = tb.get(ErrorHandler) as ErrorHandlerMock;
    authHttp = tb.get(AuthHttp);
  });

  it('getComments() should return queried comments', fakeAsync(() => {
    let result: Array<DefaultRecipient>;
    service.getDefaultRecipients().subscribe(r => result = r);
    lastConnection.mockRespond(new Response(new ResponseOptions({
      body: JSON.stringify([RECIPIENT_ONE, RECIPIENT_TWO])
    })));
    tick();
    expect(result[0]).toEqual(RECIPIENT_ONE, ' RECIPIENT_ONE should be the first recipient');
    expect(result[1]).toEqual(RECIPIENT_TWO, ' RECIPIENT_TWO should be the second recipient');
  }));

  it('getComments() should handle errors', fakeAsync(() => {
    let result: Array<DefaultRecipient>;
    spyOn(errorHandler, 'handle');
    service.getDefaultRecipients().subscribe(r => result = r, error => {});
    lastConnection.mockError(ERROR_RESPONSE);
    tick();
    expect(result).toBeUndefined();
    expect(errorHandler.handle).toHaveBeenCalledTimes(1);
  }));

  it('save() recipient without id should create new', fakeAsync(() => {
    let result: DefaultRecipient;
    let updatedRecipient: DefaultRecipient = new DefaultRecipient(RECIPIENT_ONE.id, RECIPIENT_ONE.email, RECIPIENT_ONE.applicationType);
    updatedRecipient.id = 10;

    service.saveDefaultRecipient(RECIPIENT_NEW).subscribe(r => result = r);
    lastConnection.mockRespond(new Response(new ResponseOptions({
      body: JSON.stringify(updatedRecipient)
    })));
    tick();
    expect(lastConnection.request.method).toEqual(RequestMethod.Post);
    expect(result).toEqual(updatedRecipient, 'Recipient was not saved');
  }));

  it('save() recipient with id should update', fakeAsync(() => {
    let result: DefaultRecipient;
    service.saveDefaultRecipient(RECIPIENT_ONE).subscribe(r => result = r);
    lastConnection.mockRespond(new Response(new ResponseOptions({
      body: JSON.stringify(RECIPIENT_ONE)
    })));
    tick();
    expect(lastConnection.request.method).toEqual(RequestMethod.Put);
    expect(result).toEqual(RECIPIENT_ONE, 'RECIPIENT was not saved');
  }));

  it('save() comment should handle errors', fakeAsync(() => {
    let result: DefaultRecipient;
    spyOn(errorHandler, 'handle');
    service.saveDefaultRecipient(RECIPIENT_ONE).subscribe(r => result = r, error => {});
    lastConnection.mockError(ERROR_RESPONSE);
    tick();
    expect(result).toBeUndefined();
    expect(errorHandler.handle).toHaveBeenCalledTimes(1);
  }));

  it('remove() should remove comment with matching id', fakeAsync(() => {
    let result: HttpResponse;
    service.removeDefaultRecipient(RECIPIENT_ONE.id).subscribe(r => result = r);
    lastConnection.mockRespond(new Response(new ResponseOptions({
      status: 200
    })));
    tick();
    expect(lastConnection.request.method).toEqual(RequestMethod.Delete);
    expect(result.status).toEqual(HttpStatus.OK);
  }));

  it('remove() comment should handle errors', fakeAsync(() => {
    let result: HttpResponse;
    spyOn(errorHandler, 'handle');
    service.removeDefaultRecipient(RECIPIENT_ONE.id).subscribe(r => result = r, error => {});
    lastConnection.mockError(ERROR_RESPONSE);
    tick();
    expect(result).toBeUndefined();
    expect(errorHandler.handle).toHaveBeenCalledTimes(1);
  }));

  it('remove() should do nothing when no id is passed', fakeAsync(() => {
    let result: HttpResponse;
    spyOn(authHttp, 'delete');
    service.removeDefaultRecipient(undefined).subscribe(r => result = r);
    tick();
    expect(result.status).toEqual(HttpStatus.OK);
    expect(authHttp.delete).not.toHaveBeenCalled();
  }));
});
