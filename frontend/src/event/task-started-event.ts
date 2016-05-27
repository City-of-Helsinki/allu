import {Event} from './event';

export class TaskStartedEvent extends Event {
  constructor(public taskName: string) {
    super();
  }
}
