import {Injectable} from '@angular/core';
import {EventService} from '../../event/event.service';
import {Event} from '../../event/event';
import {EventListener} from '../../event/event-listener';

@Injectable()
export class TaskManager implements EventListener {
  constructor(private eventService: EventService) {
    console.log('Task Manager created');
    this.eventService.subscribe(this);
  }

  public handle(event: Event): void {
    console.log('Task Manager handles event');
    // if (event.type === 'AddScientistEvent') {
    //   let task: AddScientistTask = new AddScientistTask(this.nameListService);
    //   task.execute(this, this.eventService, event);
    // }
  }
}
