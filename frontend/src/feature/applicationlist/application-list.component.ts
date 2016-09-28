import {Component, OnInit, OnDestroy} from '@angular/core';
import {Observable} from 'rxjs/Observable';

import {ApplicationSelectionEvent} from '../../event/selection/application-selection-event';
import {EventListener} from '../../event/event-listener';
import {EventService} from '../../event/event.service';
import {Event} from '../../event/event';

import {ApplicationsLoadEvent} from '../../event/load/applications-load-event';
import {Application} from '../../model/application/application';
import {ApplicationsAnnounceEvent} from '../../event/announce/applications-announce-event';
import {ApplicationLoadFilter} from '../../event/load/application-load-filter';
import {SearchbarUpdateEvent} from '../../event/search/searchbar-updated-event';
import {SearchbarFilter} from '../../event/search/searchbar-filter';
import {ApplicationHub} from '../../service/application/application-hub';


@Component({
  selector: 'application-list',
  template: require('./application-list.component.html'),
  styles: [
    require('./application-list.component.scss')
  ]
})

export class ApplicationListComponent implements EventListener, OnInit, OnDestroy {

  private applications: Observable<Array<Application>>;

  constructor(private eventService: EventService, private applicationHub: ApplicationHub) {
  }

  ngOnInit() {
    this.applications = this.applicationHub.applications();
  }

  ngOnDestroy() {
    this.eventService.unsubscribe(this);
  }

  public handle(event: Event): void {
  }

  jobClick(application: Application) {
    this.eventService.send(this, new ApplicationSelectionEvent(application));
  }
}
