import {Component, OnInit, OnDestroy} from '@angular/core';
import {ROUTER_DIRECTIVES, RouteConfig, Router} from '@angular/router-deprecated';

import {MdToolbar} from '@angular2-material/toolbar';
import {MD_CARD_DIRECTIVES} from '@angular2-material/card';
import {MaterializeDirective} from 'angular2-materialize';

import {TypeComponent} from '../../component/application/type/type.component';
import {OutdoorEventComponent} from '../../component/application/outdoor-event/outdoor-event.component';
import {PromotionEventComponent} from '../../component/application/promotion-event/promotion-event.component';

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
  { path: '/outdoor-event', as: 'OutdoorEventComponent', component: OutdoorEventComponent },
  { path: '/promotion-event', as: 'PromotionEventComponent', component: PromotionEventComponent }
])

export class ApplicationComponent implements EventListener, OnInit, OnDestroy {
  public applications: any;
  private types: string;
  private subtypes: any;
  private subtype: string;

  constructor(public router: Router, private eventService: EventService) {
    this.applications = [
      {
        name: 'Katutyö',
        value: 'Street',
        subtypes: [
          {name: 'Kaivuilmoitus', value: 'PromotionEventComponent'},
          {name: 'Aluevuokraus', value: 'PromotionEventComponent'},
          {name: 'Tilapäiset liikennejärjestelyt', value: 'PromotionEventComponent'}
        ]
      },
      {
        name: 'Tapahtuma',
        value: 'Event',
        subtypes: [
          {name: 'Promootio', value: 'PromotionEventComponent'},
          {name: 'Ulkoilmatapahtuma', value: 'OutdoorEventComponent'},
          {name: 'Vaalit', value: 'PromotionEventComponent'}
        ]
      }];

    this.types = undefined;
    this.subtypes = undefined;
    this.subtype = undefined;
  };

  ngOnInit(): any {
    this.eventService.subscribe(this);
    if (this.router.currentInstruction.component.routeName !== 'Type') {
      for (let application of this.applications) {
        for (let subtype of application.subtypes) {
          if (subtype.value === this.router.currentInstruction.component.routeName) {
            this.types = application.value;
            this.subtypes = application.subtypes;
            this.subtype = this.router.currentInstruction.component.routeName;
          }
        }
      }

    }
  };

  ngOnDestroy(): any {
    this.eventService.unsubscribe(this);
  }

  public handle(event: Event): void {
    if (event instanceof ApplicationsAnnounceEvent) {
      alert('Application stored!');
    }
  };

  typeSelection(value) {
    this.subtype = undefined;
    this.subtypes = undefined;
    for (let application of this.applications) {
      if (value === application.value) {
        this.subtypes = application.subtypes;
      }
    }
  };

  eventSelection(value) {
    this.router.navigate(['/Applications/' + value]);
  };
}
