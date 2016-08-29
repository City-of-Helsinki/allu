import {LoadEvent} from '../load/load-event';
import {ApplicationSearchQuery} from '../../model/search/ApplicationSearchQuery';

export class ApplicationSearchEvent extends LoadEvent {
  constructor(public applicationSearchQuery: ApplicationSearchQuery) {
    super();
  }
}
