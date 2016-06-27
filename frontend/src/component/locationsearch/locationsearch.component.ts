import {Component, OnInit, OnDestroy} from '@angular/core';
import {WorkqueueService} from '../../service/workqueue.service';
import {ROUTER_DIRECTIVES} from '@angular/router-deprecated';
import { MdAnchor, MdButton } from '@angular2-material/button';
import { MD_CARD_DIRECTIVES } from '@angular2-material/card';

import {ApplicationSelectionEvent} from '../../event/selection/application-selection-event';
import {EventListener} from '../../event/event-listener';
import {EventService} from '../../event/event.service';
import {Event} from '../../event/event';

import {ApplicationsLoadEvent} from '../../event/load/applications-load-event';
import {Application} from '../../model/application/application';
import {ApplicationsAnnounceEvent} from '../../event/announce/applications-announce-event';
import {ApplicationLoadFilter} from '../../event/load/application-load-filter';


@Component({
  selector: 'locationsearch',
  moduleId: module.id,
  template: require('./locationsearch.component.html'),
  styles: [
    require('./locationsearch.component.scss')
  ],
  directives: [MD_CARD_DIRECTIVES, MdButton]
})

export class LocationSearchComponent implements EventListener, OnInit, OnDestroy {

  private applicationsQueue: Array<Application> = [];

  constructor(private workqueueService: WorkqueueService, private eventService: EventService) {
    this.applicationsQueue = [];

  }

  ngOnInit() {
    this.eventService.subscribe(this);
    this.eventService.send(this, new ApplicationsLoadEvent(new ApplicationLoadFilter()));
  }

  ngOnDestroy() {
    this.eventService.unsubscribe(this);
  }

  public handle(event: Event): void {
    console.log('Handle and incoming LocationSearchComponent event');
    if (event instanceof ApplicationsAnnounceEvent) {
      let aaEvent = <ApplicationsAnnounceEvent>event;
      this.applicationsQueue = aaEvent.applications.slice();
    }
  }

  jobClick(application: Application) {
    this.eventService.send(this, new ApplicationSelectionEvent(application));
  }
}
