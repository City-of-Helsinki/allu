import {Component} from '@angular/core';
import {ROUTER_DIRECTIVES, RouteConfig, Router} from '@angular/router-deprecated';

import {MdToolbar} from '@angular2-material/toolbar';
import {MD_CARD_DIRECTIVES} from '@angular2-material/card';
import {MaterializeDirective} from 'angular2-materialize';

import {TypeComponent} from '../../component/application/type/type.component';
import {OutdoorEventComponent} from '../../component/application/outdoor-event/outdoor-event.component';

import {WorkqueueService} from '../../service/workqueue.service';
import {ApplicationsAnnounceEvent} from '../../event/announce/applications-announce-event';
import {Event} from '../../event/event';
import {EventListener} from '../../event/event-listener';
import {EventService} from '../../event/event.service';

@Component({
  selector: 'application',
  viewProviders: [],
  moduleId: module.id,
  template: require('./application.component.html'),
  styles: [
    require('./application.component.scss')
  ],
  directives: [
    MaterializeDirective,
    ROUTER_DIRECTIVES,
    MdToolbar,
    MD_CARD_DIRECTIVES
  ]
})

@RouteConfig([
  { path: '/', as: 'Type', component: TypeComponent }, //  useAsDefault: true }, coming soon!
  { path: '/outdoor-event', as: 'OutdoorEventComponent', component: OutdoorEventComponent }
])

export class ApplicationComponent implements EventListener {
  public application: any;
  public workqueue: WorkqueueService;
  public applicationsTypeList: any;

  constructor(public router: Router, private eventService: EventService, workqueue: WorkqueueService) {
    this.workqueue = workqueue;
    this.applicationsTypeList = [{
      name: 'Muu',
      value: 'Type'
    },
    {
      name: 'Ulkoilmatapahtuma',
      value: 'OutdoorEventComponent'
    }];
  };

  public handle(event: Event): void {
    if (event instanceof ApplicationsAnnounceEvent) {
      alert('Application stored!');
    }
  };

  typeSelection(value) {
    console.log(value);
    this.router.navigate(['/Applications/' + value]);
  };
}
