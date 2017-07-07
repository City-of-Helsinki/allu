import {DefaultRecipientHub} from '../../../src/service/recipients/default-recipient-hub';
import {fakeAsync, TestBed, tick} from '@angular/core/testing';
import {DefaultRecipientService} from '../../../src/service/recipients/default-recipient.service';
import {DefaultRecipient} from '../../../src/model/common/default-recipient';
import {ApplicationType} from '../../../src/model/application/type/application-type';
import {Observable} from 'rxjs/Observable';
import {HttpResponse, HttpStatus} from '../../../src/util/http-response';
import {RECIPIENT_ONE, RECIPIENT_TWO, RECIPIENT_NEW, RECIPIENTS_ALL} from './default-recipient-mock-values';

class DefaultRecipientServiceMock {
  getDefaultRecipients() {
    return Observable.of(RECIPIENTS_ALL);
  };
  saveDefaultRecipient(recipient: DefaultRecipient) {};
  removeDefaultRecipient(id: number) {};
};

describe('DefaultRecipientHub', () => {

  let hub: DefaultRecipientHub;
  let defaultRecipientService: DefaultRecipientServiceMock;

  beforeEach(() => {
    const tb = TestBed.configureTestingModule({
      providers: [
        { provide: DefaultRecipientService, useClass: DefaultRecipientServiceMock },
        DefaultRecipientHub
      ]
    });
    hub = tb.get(DefaultRecipientHub);
    defaultRecipientService = tb.get(DefaultRecipientService) as DefaultRecipientServiceMock;
  });

  it('should emit values when initialized', fakeAsync(() => {
    hub.defaultRecipients.subscribe(recipients => {
      expect(recipients.length).toEqual(2, 'Got unexpected number of recipients');
      expect(recipients[0]).toEqual(RECIPIENT_ONE);
      expect(recipients[1]).toEqual(RECIPIENT_TWO);
    });
  }));

  it('should emit new values on save', fakeAsync(() => {
    spyOn(defaultRecipientService, 'getDefaultRecipients').and.returnValue(Observable.of([]));
    hub.loadDefaultRecipients();
    tick();

    spyOn(defaultRecipientService, 'saveDefaultRecipient').and.returnValue(Observable.of(RECIPIENT_ONE));
    hub.saveDefaultRecipient(RECIPIENT_NEW).subscribe(recipient => {
      expect(recipient).toEqual(RECIPIENT_ONE);
    });
    tick();

    hub.defaultRecipients.last().subscribe(recipients => {
      expect(recipients.length).toEqual(1, 'Got unexpected number of recipients');
      expect(recipients[0]).toEqual(RECIPIENT_ONE);
    });
  }));

  it('should update and emit new values', fakeAsync(() => {
    spyOn(defaultRecipientService, 'getDefaultRecipients').and.returnValue(Observable.of(RECIPIENTS_ALL));
    hub.loadDefaultRecipients();
    tick();

    const updatedRecipient = new DefaultRecipient(RECIPIENT_ONE.id, 'newEmail', ApplicationType[ApplicationType.SHORT_TERM_RENTAL]);
    spyOn(defaultRecipientService, 'saveDefaultRecipient').and.returnValue(Observable.of(updatedRecipient));
    hub.saveDefaultRecipient(updatedRecipient).subscribe(recipient => expect(recipient).toEqual(updatedRecipient));
    tick();

    hub.defaultRecipients.last().subscribe(recipients => {
      expect(recipients.length).toEqual(2, 'Length should be same as before');
      expect(recipients.find((r) => r.id === updatedRecipient.id)).toEqual(updatedRecipient);
    });
  }));

  it('should remove and emit new values', fakeAsync(() => {
    spyOn(defaultRecipientService, 'getDefaultRecipients').and.returnValue(Observable.of(RECIPIENTS_ALL));
    hub.loadDefaultRecipients();
    tick();

    spyOn(defaultRecipientService, 'removeDefaultRecipient').and.returnValue(Observable.of(new HttpResponse(HttpStatus.OK)));
    hub.removeDefaultRecipient(RECIPIENT_ONE.id).subscribe(response => expect(response.status).toEqual(HttpStatus.OK));
    tick();

    hub.defaultRecipients.last().subscribe(recipients => {
      expect(recipients.length).toEqual(1, 'Recipient was not deleted');
      expect(recipients.find(r => r.id === RECIPIENT_ONE.id)).not.toBeDefined();
      expect(recipients.find(r => r.id === RECIPIENT_TWO.id)).toBeDefined();
    });
  }));
});
