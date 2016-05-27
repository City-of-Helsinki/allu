import {Event} from './event';

export class TaskFinishedEvent extends Event {
  constructor(public taskName: string) {
    super();
  }
}
