
import {SaveEvent} from './save-event';
import {Application} from '../../model/application/application';

/*
 * Event for an application to be stored into backend.
 */
export class ApplicationSaveEvent extends SaveEvent {
  constructor(public application: Application) {
    super();
  }
}
