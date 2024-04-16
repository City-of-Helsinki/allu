import {fakeAsync, TestBed, tick} from '@angular/core/testing';

import {DefaultRecipient} from '../../../src/app/model/common/default-recipient';
import {DefaultRecipientService} from '../../../src/app/service/recipients/default-recipient.service';
import {ErrorHandler} from '../../../src/app/service/error/error-handler.service';
import {RECIPIENT_NEW, RECIPIENT_ONE, RECIPIENT_TWO} from './default-recipient-mock-values';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {HttpClient} from '@angular/common/http';
import {ErrorHandlerMock} from '../../mocks';

const API_URL = '/api/default-recipients';

describe('DefaultRecipientService', () => {
  let service: DefaultRecipientService;
  let httpClient: HttpClient;
  let httpTestingController: HttpTestingController;
  let errorHandler: ErrorHandlerMock;


  beforeEach(() => {
    const tb = TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        { provide: ErrorHandler, useClass: ErrorHandlerMock},
        DefaultRecipientService
      ]
    });
    service = tb.inject(DefaultRecipientService);
    httpClient = tb.inject(HttpClient);
    httpTestingController = tb.inject(HttpTestingController);
    errorHandler = tb.inject(ErrorHandler) as ErrorHandlerMock;
  });

  it('getComments() should return queried comments', fakeAsync(() => {
    let result: Array<DefaultRecipient>;
    service.getDefaultRecipients().subscribe(r => result = r);
    const req = httpTestingController.expectOne(API_URL);
    req.flush([RECIPIENT_ONE, RECIPIENT_TWO]);
    tick();
    expect(result[0]).toEqual(RECIPIENT_ONE, ' RECIPIENT_ONE should be the first recipient');
    expect(result[1]).toEqual(RECIPIENT_TWO, ' RECIPIENT_TWO should be the second recipient');
  }));

  it('getComments() should handle errors', fakeAsync(() => {
    let result: Array<DefaultRecipient>;
    spyOn(errorHandler, 'handle').and.callThrough();
    service.getDefaultRecipients().subscribe(r => result = r, error => {});
    const req = httpTestingController.expectOne(API_URL);
    req.error(new ErrorEvent('Expected'));
    tick();
    expect(result).toBeUndefined();
    expect(errorHandler.handle).toHaveBeenCalledTimes(1);
  }));

  it('save() recipient without id should create new', fakeAsync(() => {
    let result: DefaultRecipient;
    const updatedRecipient: DefaultRecipient = new DefaultRecipient(RECIPIENT_ONE.id, RECIPIENT_ONE.email, RECIPIENT_ONE.applicationType);
    updatedRecipient.id = 10;

    service.saveDefaultRecipient(RECIPIENT_NEW).subscribe(r => result = r);
    const req = httpTestingController.expectOne(API_URL);
    req.flush(updatedRecipient);

    tick();
    expect(req.request.method).toEqual('POST');
    expect(result).toEqual(updatedRecipient, 'Recipient was not saved');
  }));


  it('save() recipient with id should update', fakeAsync(() => {
    let result: DefaultRecipient;
    service.saveDefaultRecipient(RECIPIENT_ONE).subscribe(r => result = r);
    const req = httpTestingController.expectOne(`${API_URL}/${RECIPIENT_ONE.id}`);
    req.flush(RECIPIENT_ONE);
    tick();
    expect(req.request.method).toEqual('PUT');
    expect(result).toEqual(RECIPIENT_ONE, 'RECIPIENT was not saved');
  }));


  it('save() recipien should handle errors', fakeAsync(() => {
    let result: DefaultRecipient;
    spyOn(errorHandler, 'handle').and.callThrough();
    service.saveDefaultRecipient(RECIPIENT_ONE).subscribe(r => result = r, error => {});
    const req = httpTestingController.expectOne(`${API_URL}/${RECIPIENT_ONE.id}`);
    req.error(new ErrorEvent('Expected'));
    tick();
    expect(result).toBeUndefined();
    expect(errorHandler.handle).toHaveBeenCalledTimes(1);
  }));


  it('remove() should remove recipient with matching id', fakeAsync(() => {
    service.removeDefaultRecipient(RECIPIENT_ONE.id).subscribe(() => {});
    const req = httpTestingController.expectOne(`${API_URL}/${RECIPIENT_ONE.id}`);
    tick();
    expect(req.request.method).toEqual('DELETE');
  }));

  it('remove() recipient should handle errors', fakeAsync(() => {
    spyOn(errorHandler, 'handle').and.callThrough();
    service.removeDefaultRecipient(RECIPIENT_ONE.id).subscribe(() => {}, err => {});
    const req = httpTestingController.expectOne(`${API_URL}/${RECIPIENT_ONE.id}`);
    req.error(new ErrorEvent('Expected'));
    tick();
    expect(errorHandler.handle).toHaveBeenCalledTimes(1);
  }));

  it('remove() should do nothing when no id is passed', fakeAsync(() => {
    service.removeDefaultRecipient(undefined).subscribe(r => {});
    const req = httpTestingController.expectNone(`${API_URL}/${RECIPIENT_ONE.id}`);
    tick();
  }));
});
