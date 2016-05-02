import {Component} from 'angular2/core';
import {ToolbarComponent} from '../../component/toolbar/toolbar.component';

import {MapComponent} from '../../component/map/map.component';
import {WorkqueueComponent} from '../../component/workqueue/workqueue.component';

@Component({
  selector: 'queue',
  viewProviders: [],
  moduleId: module.id,
  templateUrl: './view/queue/queue.component.html',
  styles: [
    require('./queue.component.scss')
  ],
  directives: [ToolbarComponent, MapComponent, WorkqueueComponent]
})

export class QueueComponent {}
