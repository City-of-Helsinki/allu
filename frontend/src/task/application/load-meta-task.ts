import { Injectable } from '@angular/core';
import {Task} from '../../service/task/task';
import {EventService} from '../../event/event.service';
import {EventListener} from '../../event/event-listener';
import {Event} from '../../event/event';
import {ApplicationService} from '../../service/application/application.service.ts';
import {ErrorEvent} from '../../event/error-event';
import {MetaLoadEvent} from '../../event/load/meta-load-event';
import {StructureMeta} from '../../model/application/structure-meta';
import {MetaAnnounceEvent} from '../../event/announce/meta-announce-event';

/**
 * Loads metadata from backend.
 */
@Injectable()
export class LoadMetaTask extends Task {

  constructor(private applicationService: ApplicationService) {
    super();
  }

  protected createTask(runner: EventListener, eventService: EventService, event: Event): Promise<void> {
    let mlEvent = <MetaLoadEvent>event;
    let loadPromise = this.applicationService.loadApplicationMetadata(mlEvent.applicationType);
    return loadPromise.then((structureMeta: StructureMeta) => {
      let maEvent = new MetaAnnounceEvent(structureMeta);
      eventService.send(runner, maEvent);
    }).catch((err: any) => { eventService.send(runner, new ErrorEvent(mlEvent)); });
  }
}
