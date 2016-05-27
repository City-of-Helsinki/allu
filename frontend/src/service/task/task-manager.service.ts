import {Injectable} from '@angular/core';
import {EventService} from '../../event/event.service';
import {Event} from '../../event/event';
import {EventListener} from '../../event/event-listener';
import {ApplicationsLoadEvent} from '../../event/load/applications-load-event';
import {LoadApplicationsTask} from '../../task/application/load-applications-task';
import {ApplicationService} from '../application.service';
import {ApplicationSaveEvent} from '../../event/save/application-save-event';
import {SaveApplicationTask} from '../../task/application/save-application-task';

@Injectable()
export class TaskManager implements EventListener {

  // constructor() {
  //   console.log('Task Manager created');
  // }

  constructor(private eventService: EventService, private applicationService: ApplicationService) {
    console.log('Task Manager created');
    this.eventService.subscribe(this);
  }

  public handle(event: Event): void {
    console.log('Task Manager handles event');
    if (event instanceof ApplicationsLoadEvent) {
      let task = new LoadApplicationsTask(this.applicationService);
      task.execute(this, this.eventService, event);
    }
    if (event instanceof ApplicationSaveEvent) {
      let task = new SaveApplicationTask(this.applicationService);
      task.execute(this, this.eventService, event);
    }


    // if (event.type === 'AddScientistEvent') {
    //   let task: AddScientistTask = new AddScientistTask(this.nameListService);
    //   task.execute(this, this.eventService, event);
    // }
  }
}
