import { Injectable } from '@angular/core';
import {Task} from '../../service/task/task';
import {EventService} from '../../event/event.service';
import {EventListener} from '../../event/event-listener';
import {Event} from '../../event/event';
import {ApplicationService} from '../../service/application.service';
import {ApplicationsAnnounceEvent} from '../../event/announce/applications-announce-event';
import {ApplicationsLoadEvent} from '../../event/load/applications-load-event';
import {Application} from '../../model/application/application';
import {ErrorEvent} from '../../event/error-event';

@Injectable()
export class LoadApplicationsTask extends Task {

  constructor(private applicationService: ApplicationService) {
    super();
  }

  protected createTask(runner: EventListener, eventService: EventService, event: Event): Promise<void> {
    let alEvent = <ApplicationsLoadEvent>event;
    let loadPromise = this.applicationService.listApplications(alEvent.applicationLoadFilter);
    return loadPromise.then((applications: Array<Application>) => {
      let aaEvent = new ApplicationsAnnounceEvent(applications);
      eventService.send(runner, aaEvent);
    }).catch((err: any) => { eventService.send(runner, new ErrorEvent(alEvent)); });
  }
}
