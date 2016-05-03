import {Component} from 'angular2/core';
import {WorkqueueService} from '../../service/workqueue.service';

// import {MATERIAL_DIRECTIVES, MATERIAL_PROVIDERS} from "ng2-material/all";
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
  directives: [],
  providers: [WorkqueueService]
})

export class WorkqueueComponent implements EventListener{
  marker: MarkerComponent;

  constructor(public WorkqueueService: WorkqueueService, private eventService: EventService) {
    this.eventService.subscribe(this);
  }

  public handle(event: Event): void {
    console.log('Handle and incoming WorkqueueComponent event');
  }

  jobClick(job:any) {
    //Show marker in map
    // console.log(job);

    this.eventService.send(this, new ApplicationSelectionEvent(new LatLng(job.latitude, job.longitude), job.title))

    /* This needs to be done in Perttutechture */
    //this.marker.showJobMarker();

 }
}
