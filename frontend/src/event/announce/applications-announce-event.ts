import {AnnounceEvent} from './announce-event';
import {Application} from '../../model/application/application';

export class ApplicationsAnnounceEvent extends AnnounceEvent {
  constructor(public applications: Array<Application>) {
    super();
  }
}
