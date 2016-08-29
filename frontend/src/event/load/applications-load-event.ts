import {LoadEvent} from './load-event';
import {ApplicationLoadFilter} from './application-load-filter';

/*
 * Event for requesting all applications to be loaded.
 */
export class ApplicationsLoadEvent extends LoadEvent {
  // TODO: remove filter from load. Filtering is part of application search functionality and application loading should request only
  // one application at a time
  constructor(public applicationLoadFilter: ApplicationLoadFilter) {
    super();
  }
}
