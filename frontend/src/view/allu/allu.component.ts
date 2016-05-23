import {Component} from '@angular/core';
import {ROUTER_DIRECTIVES, RouteConfig} from '@angular/router-deprecated';
import {ViewEncapsulation} from '@angular/core';

import {ToolbarComponent} from '../../component/toolbar/toolbar.component';

import {QueueComponent} from '../queue/queue.component';
import {ApplicationComponent} from '../application/application.component';

import {EventService} from '../../event/event.service';

@Component({
  selector: 'allu',
  viewProviders: [],
  moduleId: module.id,
  template: require('./allu.component.html'),
  encapsulation: ViewEncapsulation.None,
  styles: [
    require('../../assets/main.scss')
  ],
  // template: `
  // <toolbar></toolbar>
  // <router-outlet></router-outlet>
  // `,
  directives: [ROUTER_DIRECTIVES, ToolbarComponent],
  providers: [EventService]
})
@RouteConfig([
  { path: '/', name: 'FrontPage', component: QueueComponent },
  { path: '/applications', name: 'Applications', component: ApplicationComponent }
])
export class AlluComponent {}
