import {Component} from '@angular/core';
import {Router} from '@angular/router';
import {ViewEncapsulation} from '@angular/core';
import {EventService} from '../../event/event.service';
import {WorkqueueService} from '../../service/workqueue.service';
import {Login} from '../../component/login/login.component';
import {TaskManagerService} from '../../service/task/task-manager.service';
import {SearchService} from '../../service/search.service';
import {GeolocationService} from '../../service/geolocation.service';
import {ApplicationService} from '../../service/application/application.service.ts';
import {DecisionService} from '../../service/decision/decision.service';
import {AuthGuard} from '../../component/login/auth-guard.service';

@Component({
  selector: 'allu',
  viewProviders: [],
  template: require('./allu.component.html'),
  encapsulation: ViewEncapsulation.None,
  styles: [
    require('../../assets/main.scss')
  ],
  providers: [EventService, TaskManagerService, WorkqueueService]
})
export class AlluComponent {
  constructor(
    public router: Router,
    public eventService: EventService,
    public taskManager: TaskManagerService,
    private geolocationService: GeolocationService,
    private applicationService: ApplicationService,
    private decisionService: DecisionService) {}
}
