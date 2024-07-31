import {DefaultRecipientHub} from '@service/recipients/default-recipient-hub';
import {fakeAsync, TestBed, tick} from '@angular/core/testing';
import {DefaultRecipientService} from '@service/recipients/default-recipient.service';
import {DefaultRecipient} from '@model/common/default-recipient';
import {ApplicationType} from '@model/application/type/application-type';
import {RECIPIENT_ONE, RECIPIENT_TWO, RECIPIENT_NEW, RECIPIENTS_ALL} from './default-recipient-mock-values';
import {Observable, of} from 'rxjs/index';
import {last} from 'rxjs/internal/operators';
import {CurrentUserMock} from '../../mocks';
import {CurrentUser} from '@service/user/current-user';

class DefaultRecipientServiceMock {
  getDefaultRecipients() {
    return of(RECIPIENTS_ALL);
  }
  saveDefaultRecipient(recipient: DefaultRecipient): Observable<DefaultRecipient> {
    return of(recipient);
  }
  removeDefaultRecipient(id: number): Observable<{}> {
    return of({});
  }
}

const currentUserMock = CurrentUserMock.create(true, true);

describe('DefaultRecipientHub', () => {

  let hub: DefaultRecipientHub;
  let defaultRecipientService: DefaultRecipientServiceMock;

  beforeEach(() => {
    const tb = TestBed.configureTestingModule({
      providers: [
        DefaultRecipientHub,
        { provide: CurrentUser, useValue: currentUserMock },
        { provide: DefaultRecipientService, useClass: DefaultRecipientServiceMock },
      ]
    });
    hub = tb.inject(DefaultRecipientHub);
    defaultRecipientService = tb.inject(DefaultRecipientService) as DefaultRecipientServiceMock;
  });

  it('should emit values when initialized', fakeAsync(() => {
    hub.defaultRecipients.subscribe(recipients => {
      expect(recipients.length).toEqual(2, 'Got unexpected number of recipients');
      expect(recipients[0]).toEqual(RECIPIENT_ONE);
      expect(recipients[1]).toEqual(RECIPIENT_TWO);
    });
  }));

  it('should emit new values on save', fakeAsync(() => {
    spyOn(defaultRecipientService, 'getDefaultRecipients').and.returnValue(of([]));
    hub.loadDefaultRecipients();
    tick();

    spyOn(defaultRecipientService, 'saveDefaultRecipient').and.returnValue(of(RECIPIENT_ONE));
    hub.saveDefaultRecipient(RECIPIENT_NEW).subscribe(recipient => {
      expect(recipient).toEqual(RECIPIENT_ONE);
    });
    tick();

    hub.defaultRecipients.pipe(last()).subscribe(recipients => {
      expect(recipients.length).toEqual(1, 'Got unexpected number of recipients');
      expect(recipients[0]).toEqual(RECIPIENT_ONE);
    });
  }));

  it('should update and emit new values', fakeAsync(() => {
    spyOn(defaultRecipientService, 'getDefaultRecipients').and.returnValue(of(RECIPIENTS_ALL));
    hub.loadDefaultRecipients();
    tick();

    const updatedRecipient = new DefaultRecipient(RECIPIENT_ONE.id, 'newEmail', ApplicationType[ApplicationType.SHORT_TERM_RENTAL]);
    spyOn(defaultRecipientService, 'saveDefaultRecipient').and.returnValue(of(updatedRecipient));
    hub.saveDefaultRecipient(updatedRecipient).subscribe(recipient => expect(recipient).toEqual(updatedRecipient));
    tick();

    hub.defaultRecipients.pipe(last()).subscribe(recipients => {
      expect(recipients.length).toEqual(2, 'Length should be same as before');
      expect(recipients.find((r) => r.id === updatedRecipient.id)).toEqual(updatedRecipient);
    });
  }));

  it('should remove and emit new values', fakeAsync(() => {
    spyOn(defaultRecipientService, 'getDefaultRecipients').and.returnValue(of(RECIPIENTS_ALL));
    hub.loadDefaultRecipients();
    tick();

    spyOn(defaultRecipientService, 'removeDefaultRecipient').and.returnValue(of({}));
    hub.removeDefaultRecipient(RECIPIENT_ONE.id).subscribe(response => expect(response).toEqual({}));
    tick();

    hub.defaultRecipients.pipe(last()).subscribe(recipients => {
      expect(recipients.length).toEqual(1, 'Recipient was not deleted');
      expect(recipients.find(r => r.id === RECIPIENT_ONE.id)).not.toBeDefined();
      expect(recipients.find(r => r.id === RECIPIENT_TWO.id)).toBeDefined();
    });
  }));
});
