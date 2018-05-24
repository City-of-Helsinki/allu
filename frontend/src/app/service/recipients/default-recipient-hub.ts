import {Injectable} from '@angular/core';
import {DefaultRecipientService} from './default-recipient.service';
import {DefaultRecipient} from '../../model/common/default-recipient';
import {ArrayUtil} from '../../util/array-util';
import {ReplaySubject} from 'rxjs';
import {map, share, tap} from 'rxjs/internal/operators';

@Injectable()
export class DefaultRecipientHub {
  private latestRecipients: Array<DefaultRecipient> = [];
  private defaultRecipients$ = new ReplaySubject<Array<DefaultRecipient>>();

  constructor(private service: DefaultRecipientService) {
    this.loadDefaultRecipients();
    this.defaultRecipients.subscribe(recipients => this.latestRecipients = recipients);
  }

  get defaultRecipients() {
    return this.defaultRecipients$.asObservable().pipe(share());
  }

  defaultRecipientsByApplicationType(type: string) {
    return this.defaultRecipients.pipe(
      map(recipients => recipients.filter(dr => dr.applicationType === type))
    );
  }

  saveDefaultRecipient(recipient: DefaultRecipient) {
    return this.service.saveDefaultRecipient(recipient).pipe(
      tap(saved => {
        const updated = ArrayUtil.createOrReplace(
          this.latestRecipients,
          saved,
          (item: DefaultRecipient) => item.id === saved.id);
        this.defaultRecipients$.next(updated);
      }));
  }

  removeDefaultRecipient(id: number) {
    return this.service.removeDefaultRecipient(id).pipe(
      tap(response => {
        const updated = this.latestRecipients.filter(dr => dr.id !== id);
        this.defaultRecipients$.next(updated);
      }));
  }

  loadDefaultRecipients() {
    this.service.getDefaultRecipients().subscribe(recipients => this.defaultRecipients$.next(recipients));
  }
}
