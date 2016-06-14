import {Component, OnInit, OnDestroy} from '@angular/core';
import {WorkqueueService} from '../../service/workqueue.service';
import {ROUTER_DIRECTIVES} from '@angular/router-deprecated';
import { MdAnchor, MdButton } from '@angular2-material/button';
import { MD_CARD_DIRECTIVES } from '@angular2-material/card';
import {MarkerComponent} from '../marker/marker.component';
import {SELECT_DIRECTIVES} from 'ng2-select/ng2-select';
import {MaterializeDirective} from 'angular2-materialize';

import {ApplicationSelectionEvent} from '../../event/selection/application-selection-event';
import {EventListener} from '../../event/event-listener';
import {EventService} from '../../event/event.service';
import {Event} from '../../event/event';

import {LatLng} from '../../model/location/latlng';
import {ApplicationsLoadEvent} from '../../event/load/applications-load-event';
import {Application} from '../../model/application/application';
import {ApplicationsAnnounceEvent} from '../../event/announce/applications-announce-event';
import {TaskManagerService} from '../../service/task/task-manager.service';
import {ApplicationLoadFilter} from '../../event/load/application-load-filter';


@Component({
  selector: 'workqueue',
  moduleId: module.id,
  template: require('./workqueue.component.html'),
  styles: [
    require('./workqueue.component.scss')
  ],
  directives: [MD_CARD_DIRECTIVES, MaterializeDirective, MdButton]
})

export class WorkQueueComponent implements EventListener, OnInit, OnDestroy {
  private applicationsQueue: Array<Application> = [];
  private items: Array<string> = ['Ensimmäinen', 'Toinen', 'Kolmas', 'Neljäs', 'Viides'];

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

  public selected(value: any): void {
    console.log('Selected value is: ', value);
  }

  public handle(event: Event): void {
    console.log('Handle and incoming WorkqueueComponent event');
    if (event instanceof ApplicationsAnnounceEvent) {
      let aaEvent = <ApplicationsAnnounceEvent>event;
      this.applicationsQueue = aaEvent.applications.slice();
    }
  }

  jobClick(job: Application) {
    this.eventService.send(this, new ApplicationSelectionEvent(job));
  }
}
