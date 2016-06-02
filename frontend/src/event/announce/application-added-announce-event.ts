import {AnnounceEvent} from './announce-event';
import {Application} from '../../model/application/application';

export class ApplicationAddedAnnounceEvent extends AnnounceEvent {
  constructor(public application: Application) {
    super();
  }
}
