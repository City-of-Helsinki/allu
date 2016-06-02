import {LoadEvent} from './load-event';

/*
 * Event for requesting all applications to be loaded.
 */
export class ApplicationsLoadEvent extends LoadEvent {
  constructor(public handler: string) {
    super();
  }
}
