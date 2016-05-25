import {Component} from '@angular/core';
import {ROUTER_DIRECTIVES, RouteConfig, Router} from '@angular/router-deprecated';
import {ViewEncapsulation} from '@angular/core';

import {ToolbarComponent} from '../../component/toolbar/toolbar.component';

import {QueueComponent} from '../queue/queue.component';
import {ApplicationComponent} from '../application/application.component';

import {EventService} from '../../event/event.service';
import {WorkqueueService} from '../../service/workqueue.service';
import {Login} from '../../component/login/login.component';
import {LoginRouterOutlet} from '../../component/login/login-router-outlet.component';
import {TaskManager} from '../../service/task/task-manager.service';

@Component({
  selector: 'allu',
  viewProviders: [],
  moduleId: module.id,
  template: require('./allu.component.html'),
  encapsulation: ViewEncapsulation.None,
  styles: [
    require('../../assets/main.scss')
  ],
  directives: [ToolbarComponent, LoginRouterOutlet],
  providers: [EventService, TaskManager, WorkqueueService]
})
@RouteConfig([
  { path: '/', name: 'FrontPage', component: QueueComponent },
  { path: '/applications', name: 'Applications', component: ApplicationComponent },
  { path: '/login', name: 'Login', component: Login }
])
export class AlluComponent {
  constructor(public router: Router, public eventService: EventService, public taskManager: TaskManager) {}
}
