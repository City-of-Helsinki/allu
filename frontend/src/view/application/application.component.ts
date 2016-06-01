import {Component} from '@angular/core';
import {FORM_DIRECTIVES} from '@angular/common';
import {ROUTER_DIRECTIVES, RouteConfig, Router} from '@angular/router-deprecated';

import {MdToolbar} from '@angular2-material/toolbar';
import {MD_CARD_DIRECTIVES} from '@angular2-material/card';

import {ToolbarComponent} from '../../component/toolbar/toolbar.component';
import {TypeComponent} from '../../component/application/type/type.component';
import {OutdoorEventComponent} from '../../component/application/outdoor-event/outdoor-event.component';

import {MapComponent} from '../../component/map/map.component';
import {WorkqueueComponent} from '../../component/workqueue/workqueue.component';


import {WorkqueueService} from '../../service/workqueue.service';
import {ApplicationsAnnounceEvent} from '../../event/announce/applications-announce-event';
import {Event} from '../../event/event';
import {EventListener} from '../../event/event-listener';
import {Application} from '../../model/application/application';
import {Customer} from '../../model/customer/customer';
import {EventService} from '../../event/event.service';
import {ApplicationSaveEvent} from '../../event/save/application-save-event';

@Component({
  selector: 'application',
  viewProviders: [],
  moduleId: module.id,
  template: require('./application.component.html'),
  styles: [
    require('./application.component.scss')
  ],
  directives: [
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
  public applications: any;

  constructor(public router: Router, private eventService: EventService, workqueue: WorkqueueService) {
    this.workqueue = workqueue;
    this.applications = [{
      name: 'Muu',
      id: 'Type'
    },
    {
      name: 'Ulkoilmatapahtuma',
      id: 'OutdoorEventComponent'
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
