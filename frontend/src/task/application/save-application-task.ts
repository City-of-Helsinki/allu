import { Injectable } from '@angular/core';
import {Task} from '../../service/task/task';
import {EventService} from '../../event/event.service';
import {EventListener} from '../../event/event-listener';
import {Event} from '../../event/event';
import {ApplicationService} from '../../service/application.service';
import {ApplicationsAnnounceEvent} from '../../event/announce/applications-announce-event';
import {ApplicationSaveEvent} from '../../event/save/application-save-event';
import {Application} from '../../model/application/application';
import {ErrorEvent} from '../../event/error-event';
import {ApplicationAddedAnnounceEvent} from '../../event/announce/application-added-announce-event';

@Injectable()
export class SaveApplicationTask extends Task {

  constructor(private applicationService: ApplicationService) {
    super();
  }

  protected createTask(runner: EventListener, eventService: EventService, event: Event): Promise<void> {
    let saEvent = <ApplicationSaveEvent>event;
    let addPromise = this.applicationService.addApplication(saEvent.application);

    return addPromise.then((appl: Application) => {
      let aaEvent = new ApplicationAddedAnnounceEvent(appl);
      eventService.send(runner, aaEvent);
    }).catch((err: any) => { console.log(err); eventService.send(runner, new ErrorEvent(saEvent)); });
  }
}
