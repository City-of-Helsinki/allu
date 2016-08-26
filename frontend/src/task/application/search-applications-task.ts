import {Injectable} from '@angular/core';
import {Task} from '../../service/task/task';
import {SearchService} from '../../service/search.service';
import {EventService} from '../../event/event.service';
import {ApplicationSearchEvent} from '../../event/search/application-search-event';
import {Application} from '../../model/application/application';
import {ApplicationsAnnounceEvent} from '../../event/announce/applications-announce-event';
import {EventListener} from '../../event/event-listener';
import {Event} from '../../event/event';
import {ErrorEvent} from '../../event/error-event';

@Injectable()
export class SearchApplicationsTask extends Task {

  constructor(private searchService: SearchService) {
    super();
  }

  protected createTask(runner: EventListener, eventService: EventService, event: Event): Promise<void> {
    console.log('SearchApplicationsTask created');
    let asEvent = <ApplicationSearchEvent>event;
    let loadPromise = this.searchService.searchApplication(asEvent.applicationSearchQuery);
    return loadPromise.then((applications: Array<Application>) => {
      let aaEvent = new ApplicationsAnnounceEvent(applications);
      eventService.send(runner, aaEvent);
    }).catch((err: any) => { eventService.send(runner, new ErrorEvent(asEvent)); });
  }
}
