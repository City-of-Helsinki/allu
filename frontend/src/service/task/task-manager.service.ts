import {Injectable} from 'angular2/core';
import {EventListener} from '../event/event-listener';
import {Event} from '../event/event';
import {EventService} from '../event/event.service';
import {AddScientistTask} from './hierarchy/add-scientist-task';
import {NameListService} from '../name-list.service';

@Injectable()
export class TaskManager implements EventListener {
  constructor(private eventService: EventService, private nameListService: NameListService) {
    console.log('Task Manager created');
    this.eventService.subscribe(this);
  }

  public handle(event: Event): void {
    console.log('Task Manager handles event: ' + event.type);
    if (event.type === 'AddScientistEvent') {
      let task: AddScientistTask = new AddScientistTask(this.nameListService);
      task.execute(this, this.eventService, event);
    }
  }
}
