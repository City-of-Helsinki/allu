
import {EventListener} from '../../event/event-listener';
import {EventService} from '../../event/event.service';
import {Event} from '../../event/event';
import {TaskStartedEvent} from '../../event/task-started-event';
import {TaskFinishedEvent} from '../../event/task-finished-event';

export abstract class Task {

  public execute(runner: EventListener, eventService: EventService, event: Event): void {
    eventService.send(runner, new TaskStartedEvent(this.getTaskName()));
    this.createTask(runner, eventService, event).then(() => eventService.send(runner, new TaskFinishedEvent(this.getTaskName())));
  }

  protected abstract createTask(runner: EventListener, eventService: EventService, event: Event): Promise<void>;

  private getTaskName(): string {
    return this.constructor.toString().match(/\w+/g)[1];
  }
}
