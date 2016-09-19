import { Injectable } from '@angular/core';
import {Task} from '../../service/task/task';
import {EventService} from '../../event/event.service';
import {EventListener} from '../../event/event-listener';
import {Event} from '../../event/event';
import {ApplicationService} from '../../service/application/application.service.ts';
import {ApplicationsAnnounceEvent} from '../../event/announce/applications-announce-event';
import {ApplicationSaveEvent} from '../../event/save/application-save-event';
import {Application} from '../../model/application/application';
import {ErrorEvent} from '../../event/error-event';
import {ApplicationAddedAnnounceEvent} from '../../event/announce/application-added-announce-event';

@Injectable()
/*
 * Task for saving new or updating existing applications.
 * Events:
 * - ApplicationAddedAnnounceEvent in case new application is created to backend
 * - ApplicationsAnnounceEvent in case existing application was updated
 */
export class SaveApplicationTask extends Task {

  constructor(private applicationService: ApplicationService) {
    super();
  }

  protected createTask(runner: EventListener, eventService: EventService, event: Event): Promise<void> {
    let saEvent = <ApplicationSaveEvent>event;
    let isNewApplication = (saEvent.application.id) ? false : true;

    if (isNewApplication) {
      let addPromise = this.applicationService.addApplication(saEvent.application);
      return addPromise.then((appl: Application) => {
        let aaaEvent = new ApplicationAddedAnnounceEvent(appl);
        eventService.send(runner, aaaEvent);
      }).catch((err: any) => { console.log(err); eventService.send(runner, new ErrorEvent(saEvent)); });
    } else {
      let updatePromise = this.applicationService.updateApplication(saEvent.application);
      return updatePromise.then((appl: Application) => {
        let aaEvent = new ApplicationsAnnounceEvent([appl]);
        eventService.send(runner, aaEvent);
      }).catch((err: any) => { console.log(err); eventService.send(runner, new ErrorEvent(saEvent)); });
    }
  }
}
