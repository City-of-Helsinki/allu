import {Component, OnInit, OnDestroy} from '@angular/core';
import {WorkqueueService} from '../../service/workqueue.service';
import {ROUTER_DIRECTIVES} from '@angular/router-deprecated';
import { MdAnchor, MdButton } from '@angular2-material/button';
import { MD_CARD_DIRECTIVES } from '@angular2-material/card';
import {MarkerComponent} from '../marker/marker.component';

import {ApplicationSelectionEvent} from '../../event/selection/application-selection-event';
import {EventListener} from '../../event/event-listener';
import {EventService} from '../../event/event.service';
import {Event} from '../../event/event';

import {LatLng} from '../../model/location/latlng';
import {ApplicationsLoadEvent} from '../../event/load/applications-load-event';
import {Application} from '../../model/application/application';
import {ApplicationsAnnounceEvent} from '../../event/announce/applications-announce-event';
import {TaskManagerService} from '../../service/task/task-manager.service';


@Component({
  selector: 'workqueue',
  moduleId: module.id,
  template: require('./workqueue.component.html'),
  styles: [
    require('./workqueue.component.scss')
  ],
  directives: [MD_CARD_DIRECTIVES, MdButton]
})

export class WorkqueueComponent implements EventListener, OnInit, OnDestroy {
  marker: MarkerComponent;
  joblist: any;
  workqueue: WorkqueueService;

  private applicationsQueue: Array<Application> = [];

  constructor(private workqueueService: WorkqueueService, private eventService: EventService) {
    this.applicationsQueue = [];

  }

  ngOnInit() {
    this.eventService.subscribe(this);
    this.eventService.send(this, new ApplicationsLoadEvent('Minna'));
  }

  ngOnDestroy() {
    this.eventService.unsubscribe(this);
  }

  public handle(event: Event): void {
    console.log('Handle and incoming WorkqueueComponent event');
    if (event instanceof ApplicationsAnnounceEvent) {
      let aaEvent = <ApplicationsAnnounceEvent>event;
      this.applicationsQueue = aaEvent.applications.slice();
      this.applicationsQueue.forEach((app) => app.area = JSON.parse(localStorage.getItem('application')));
    }
  }

  jobClick(job: any) {
    this.eventService.send(this, new ApplicationSelectionEvent(job.area));
  }
}
