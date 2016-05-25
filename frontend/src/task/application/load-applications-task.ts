import { Injectable } from '@angular/core';
import {Task} from '../../service/task/task';
import {EventService} from '../../event/event.service';
import {EventListener} from '../../event/event-listener';
import {Event} from '../../event/event';
import {ApplicationService} from '../../service/application.service';
import {ApplicationsAnnounceEvent} from '../../event/announce/applications-announce-event';

@Injectable()
export class LoadApplicationsTask extends Task {

  constructor(private applicationService: ApplicationService) {
    super();
  }

  public run(runner: EventListener, eventService: EventService, event: Event): void {
    let applications = this.applicationService.listApplications();
    let aaEvent = new ApplicationsAnnounceEvent(applications);
    eventService.send(runner, aaEvent);
  }
}
