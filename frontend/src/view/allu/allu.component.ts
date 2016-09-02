import {Component} from '@angular/core';
import {ROUTER_DIRECTIVES, RouteConfig, Router} from '@angular/router-deprecated';
import {ViewEncapsulation} from '@angular/core';

import {ToolbarComponent} from '../../component/toolbar/toolbar.component';

import {MapSearchComponent} from '../mapsearch/mapsearch.component';
import {ApplicationComponent} from '../application/application.component';
import {LocationComponent} from '../../component/location/location.component';
import {WorkQueueComponent} from '../../component/workqueue/workqueue.component';
import {SearchComponent} from '../../component/search/search.component';
import {SummaryComponent} from '../../component/application/summary/summary.component';

import {EventService} from '../../event/event.service';
import {WorkqueueService} from '../../service/workqueue.service';
import {Login} from '../../component/login/login.component';
import {LoginRouterOutlet} from '../../component/login/login-router-outlet.component';
import {TaskManagerService} from '../../service/task/task-manager.service';
import {SearchService} from '../../service/search.service';
import {GeolocationService} from '../../service/geolocation.service';
import {ApplicationService} from '../../service/application.service';

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
  providers: [EventService, TaskManagerService, WorkqueueService]
})
@RouteConfig([
  { path: '/', as: 'FrontPage', component: MapSearchComponent },
  { path: '/applications/...', as: 'Applications', component: ApplicationComponent },
  { path: '/workqueue', as: 'Workqueue', component: WorkQueueComponent },
  { path: '/location', as: 'NewLocation', component: LocationComponent },
  { path: '/location/:id', as: 'Location', component: LocationComponent },
  { path: '/summary/:id', as: 'Summary', component: SummaryComponent },
  { path: '/search', as: 'Search', component: SearchComponent },
  { path: '/login', as: 'Login', component: Login }
])
export class AlluComponent {
  constructor(
    public router: Router,
    public eventService: EventService,
    public taskManager: TaskManagerService,
    private geolocationService: GeolocationService,
    private applicationService: ApplicationService) {}
}
