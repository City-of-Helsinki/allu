import {Injectable} from '@angular/core';
import {DefaultRecipientService} from './default-recipient.service';
import {DefaultRecipient} from '../../model/common/default-recipient';
import {ArrayUtil} from '../../util/array-util';
import {ReplaySubject} from 'rxjs/ReplaySubject';

@Injectable()
export class DefaultRecipientHub {
  private latestRecipients: Array<DefaultRecipient> = [];
  private defaultRecipients$ = new ReplaySubject<Array<DefaultRecipient>>();

  constructor(private service: DefaultRecipientService) {
    this.loadDefaultRecipients();
    this.defaultRecipients.subscribe(recipients => this.latestRecipients = recipients);
  }

  get defaultRecipients() {
    return this.defaultRecipients$
      .asObservable()
      .share();
  }

  defaultRecipientsByApplicationType(type: string) {
    return this.defaultRecipients
      .map(recipients => recipients.filter(dr => dr.applicationType === type));
  }

  saveDefaultRecipient(recipient: DefaultRecipient) {
    return this.service.saveDefaultRecipient(recipient)
      .do(saved => {
        const updated = ArrayUtil.createOrReplace(
          this.latestRecipients,
          saved,
          (item: DefaultRecipient) => item.id === saved.id);
        this.defaultRecipients$.next(updated);
      });
  }

  removeDefaultRecipient(id: number) {
    return this.service.removeDefaultRecipient(id)
      .do(response => {
        const updated = this.latestRecipients.filter(dr => dr.id !== id);
        this.defaultRecipients$.next(updated);
      });
  }

  loadDefaultRecipients() {
    this.service.getDefaultRecipients().subscribe(recipients => this.defaultRecipients$.next(recipients));
  }
}
