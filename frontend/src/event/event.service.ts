import {Injectable} from '@angular/core';

import {EventListener} from './event-listener';
import {Event} from './event';
import {Subject} from 'rxjs/Subject';
import {listeners} from 'cluster';

// @Injectable()
export class EventService {

  private listeners: Array<EventListener> = [];
  private eventQueue: Subject<Event> = new Subject();

  constructor() {
    this.eventQueue.subscribe(
      (e) => this.processQueue(e));
  }

  subscribe(eventListener: EventListener): void {
    this.listeners.push(eventListener);
    console.log('EventService.subscribe', eventListener);
    console.log(this.listeners);
  }

  unsubscribe(eventListener: EventListener): void {
    let index = this.listeners.indexOf(eventListener, 0);
    console.log('Unsubscribe index: ' + index);
    if (index > -1) {
      this.listeners.splice(index, 1);
    }
  }

  send(eventSender: EventListener, event: Event): void {
    console.log('EventService.send', event);
    event.sender = eventSender;
    this.eventQueue.next(event);
  }

  private processQueue(event: Event) {
    for (let el of this.listeners) {
      console.log(el);

      if (el !== event.sender) {
        el.handle(event);
        console.log('EventService.emptyQueue sending event', el);
      }
    }
  }
}
