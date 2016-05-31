import {Component} from '@angular/core';
import {FORM_DIRECTIVES} from '@angular/common';
import {ROUTER_DIRECTIVES, RouteConfig, Router} from '@angular/router-deprecated';

import {MdToolbar} from '@angular2-material/toolbar';

import {ToolbarComponent} from '../../component/toolbar/toolbar.component';
import {TypeComponent} from '../../component/application/type/type.component';
import {LocationComponent} from '../../component/application/location/location.component';


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
    MdToolbar
  ]
})

@RouteConfig([
  { path: '/', as: 'Type', component: TypeComponent }, //  useAsDefault: true }, coming soon!
  { path: '/location', as: 'Location', component: LocationComponent }
])

export class ApplicationComponent implements EventListener {
  public application: any;
  public workqueue: WorkqueueService;

  constructor(public router: Router, private eventService: EventService, workqueue: WorkqueueService) {
    this.workqueue = workqueue;
  };

  public handle(event: Event): void {
    if (event instanceof ApplicationsAnnounceEvent) {
      alert('Application stored!');
    }
  }
}
