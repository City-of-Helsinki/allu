
import {EventListener} from '../../event/event-listener';
import {EventService} from '../../event/event.service';
import {Event} from '../../event/event';
export abstract class Task {
  public execute(runner: EventListener, eventService: EventService, event: Event): void {
    // TODO: fire start task event
    this.run(runner, eventService, event);
    // TODO: fire end task event
  }

  public abstract run(runner: EventListener, eventService: EventService, event: Event): void;
}
