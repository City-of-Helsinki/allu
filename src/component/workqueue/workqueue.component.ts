import {Component} from 'angular2/core';
import {WorkqueueService} from '../../service/workqueue.service';

// import {MATERIAL_DIRECTIVES, MATERIAL_PROVIDERS} from "ng2-material/all";

import {MarkerComponent} from '../marker/marker.component';

@Component({
  selector: 'workqueue',
  moduleId: module.id,
  templateUrl: './component/workqueue/workqueue.component.html',
  styles: [
    require('./workqueue.component.scss')
  ],
  directives: [],
  providers: [WorkqueueService]
})
export class WorkqueueComponent {
  marker: MarkerComponent;
  constructor(public WorkqueueService: WorkqueueService) {}

  jobClick(job:any) {
    //Show marker in map
    console.log(job);
    /* This needs to be done in Perttutechture */
    //this.marker.showJobMarker();

 }
}
