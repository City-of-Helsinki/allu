import {SelectionEvent} from './selection-event';
import {Application} from '../../model/application/application';

export class ApplicationSelectionEvent extends SelectionEvent {
  constructor(public application: Application) {
    super();
  }
}
