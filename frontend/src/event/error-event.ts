import {Event} from './event';

export class ErrorEvent extends Event {
  constructor(public originalEvent: Event) {
    super();
  }
}
