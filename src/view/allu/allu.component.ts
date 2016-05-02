import {Component} from 'angular2/core';
import {ROUTER_DIRECTIVES, RouteConfig} from 'angular2/router';
import {ViewEncapsulation} from 'angular2/core'

import {ToolbarComponent} from '../../component/toolbar/toolbar.component';

import {QueueComponent} from '../queue/queue.component';

@Component({
  selector: 'allu',
  viewProviders: [],
  moduleId: module.id,
  templateUrl: './view/allu/allu.component.html',
  encapsulation: ViewEncapsulation.None,
  styles: [
    require('../../assets/main.scss')
  ],
  // template: `
  // <toolbar></toolbar>
  // <router-outlet></router-outlet>
  // `,
  directives: [ROUTER_DIRECTIVES, ToolbarComponent]
})
@RouteConfig([
  { path: '/', name: 'Queue', component: QueueComponent },
  // { path: '/map', name: 'Map', component: FullMapComponent }
])
export class AlluComponent {}
