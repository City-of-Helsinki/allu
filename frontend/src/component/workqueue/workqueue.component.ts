import {Component} from '@angular/core';
import {WorkqueueService} from '../../service/workqueue.service';
import {ROUTER_DIRECTIVES} from 'angular2/router';

import { MdAnchor, MdButton } from '@angular2-material/button';
import { MD_CARD_DIRECTIVES } from '@angular2-material/card';
// import { MdCheckbox } from '@angular2-material/checkbox';
// import { MD_INPUT_DIRECTIVES } from '@angular2-material/input';
// import { MD_LIST_DIRECTIVES } from '@angular2-material/list';
// import { MdProgressBar } from '@angular2-material/progress-bar';
// import { MdProgressCircle, MdSpinner } from '@angular2-material/progress-circle';
// import { MdRadioButton, MdRadioDispatcher, MdRadioGroup } from '@angular2-material/radio';
// import { MD_SIDENAV_DIRECTIVES } from '@angular2-material/sidenav';
// import { MdToolbar } from '@angular2-material/toolbar';

import {MarkerComponent} from '../marker/marker.component';

import {ApplicationSelectionEvent} from '../../event/selection/application-selection-event';
import {EventListener} from '../../event/event-listener';
import {EventService} from '../../event/event.service';
import {Event} from '../../event/event';

import {LatLng} from '../../model/location/latlng';

@Component({
  selector: 'workqueue',
  moduleId: module.id,
  template: require('./workqueue.component.html'),
  styles: [
    require('./workqueue.component.scss')
  ],
  directives: [MD_CARD_DIRECTIVES, MdButton],
  providers: [WorkqueueService]
})

export class WorkqueueComponent implements EventListener {
  marker: MarkerComponent;

  constructor(public workqueueService: WorkqueueService, private eventService: EventService) {
    this.eventService.subscribe(this);
  }

  public handle(event: Event): void {
    console.log('Handle and incoming WorkqueueComponent event');
  }

  jobClick(job: any) {
    // Show marker in map
    // console.log(job);

    this.eventService.send(this, new ApplicationSelectionEvent(new LatLng(job.latitude, job.longitude), job.title));

    /* This needs to be done in Perttutechture */
    // this.marker.showJobMarker();

 }
}
