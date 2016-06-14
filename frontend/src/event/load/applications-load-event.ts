import {LoadEvent} from './load-event';
import {ApplicationLoadFilter} from './application-load-filter';

/*
 * Event for requesting all applications to be loaded.
 */
export class ApplicationsLoadEvent extends LoadEvent {
  constructor(public applicationLoadFilter: ApplicationLoadFilter) {
    super();
  }
}
