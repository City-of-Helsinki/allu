import {EventListener} from './event-listener';
export class Event {

  public sender: EventListener;

  constructor(public type: string) {}
}
