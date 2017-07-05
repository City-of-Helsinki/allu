import {Injectable} from '@angular/core';
import '../../rxjs-extensions.ts';
import {DefaultRecipientService} from './default-recipient.service';
import {DefaultRecipient} from '../../model/common/default-recipient';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {ArrayUtil} from '../../util/array-util';

@Injectable()
export class DefaultRecipientHub {
  private defaultRecipients$ = new BehaviorSubject<Array<DefaultRecipient>>([]);

  constructor(private service: DefaultRecipientService) {
  }

  get defaultRecipients() {
    return this.defaultRecipients$
      .asObservable()
      .share();
  }

  defaultRecipientsByType(type: string) {
    return this.defaultRecipients
      .map(recipients => recipients.filter(dr => dr.applicationType === type));
  }

  saveDefaultRecipient(recipient: DefaultRecipient) {
    return this.service.saveDefaultRecipient(recipient)
      .do(saved => {
        const updated = ArrayUtil.createOrReplace(
          this.defaultRecipients$.getValue(),
          saved,
          (item: DefaultRecipient) => item.id === saved.id);
        this.defaultRecipients$.next(updated);
      });
  }

  removeDefaultRecipient(id: number) {
    return this.service.removeDefaultRecipient(id)
      .do(response => {
        const updated = this.defaultRecipients$.getValue().filter(dr => dr.id !== id);
        this.defaultRecipients$.next(updated);
      });
  }

  loadDefaultRecipients() {
    this.service.getDefaultRecipients().subscribe(recipients => this.defaultRecipients$.next(recipients));
  }
}
