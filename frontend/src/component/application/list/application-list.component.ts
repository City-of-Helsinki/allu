import {Component, OnInit, OnDestroy} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {ROUTER_DIRECTIVES} from '@angular/router-deprecated';
import { MdAnchor, MdButton } from '@angular2-material/button';
import { MD_CARD_DIRECTIVES } from '@angular2-material/card';

import {WorkqueueService} from '../../../service/workqueue.service';
import {ApplicationSelectionEvent} from '../../../event/selection/application-selection-event';
import {EventListener} from '../../../event/event-listener';
import {EventService} from '../../../event/event.service';
import {Event} from '../../../event/event';

import {ApplicationsLoadEvent} from '../../../event/load/applications-load-event';
import {Application} from '../../../model/application/application';
import {ApplicationsAnnounceEvent} from '../../../event/announce/applications-announce-event';
import {ApplicationLoadFilter} from '../../../event/load/application-load-filter';
import {SearchbarUpdateEvent} from '../../../event/search/searchbar-updated-event';
import {SearchbarFilter} from '../../../event/search/searchbar-filter';
import {ApplicationHub} from '../../../service/application-hub';


@Component({
  selector: 'application-list',
  moduleId: module.id,
  template: require('./application-list.component.html'),
  styles: [
    require('./application-list.component.scss')
  ],
  directives: [MD_CARD_DIRECTIVES, MdButton]
})

export class ApplicationListComponent implements EventListener, OnInit, OnDestroy {

  private applications: Observable<Array<Application>>;

  constructor(private workqueueService: WorkqueueService, private eventService: EventService, private applicationHub: ApplicationHub) {
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
