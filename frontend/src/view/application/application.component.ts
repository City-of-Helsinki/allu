import {Component, OnInit} from '@angular/core';
import {ROUTER_DIRECTIVES, RouteConfig, Router} from '@angular/router-deprecated';

import {MdToolbar} from '@angular2-material/toolbar';
import {MD_CARD_DIRECTIVES} from '@angular2-material/card';
import {MaterializeDirective} from 'angular2-materialize';

import {ProgressStep, ProgressMode, ProgressbarComponent} from '../../component/progressbar/progressbar.component';

import {TypeComponent} from '../../component/application/type/type.component';
import {OutdoorEventComponent} from '../../component/application/outdoor-event/outdoor-event.component';
import {PromotionEventComponent} from '../../component/application/promotion-event/promotion-event.component';

import {ApplicationsAnnounceEvent} from '../../event/announce/applications-announce-event';
import {Event} from '../../event/event';

@Component({
  selector: 'application',
  viewProviders: [],
  moduleId: module.id,
  template: require('./application.component.html'),
  styles: [
    require('./application.component.scss')
  ],
  directives: [
    ProgressbarComponent,
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

export class ApplicationComponent implements OnInit {
  public applications: any;
  private types: string;
  private subtypes: any;
  private subtype: string;
  private progressStep: number;
  private progressMode: number;

  constructor(public router: Router) {
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

    this.progressStep = ProgressStep.INFORMATION;
    this.progressMode = ProgressMode.NEW;
  };

  ngOnInit(): any {
    let routeName = this.router.currentInstruction.component.routeName;

    if (routeName !== 'Type') {
      this.applications
        .filter(application => application.subtypes.some(subtype => subtype.value === routeName))
        .foreach(application => {
          this.types = application.value;
          this.subtypes = application.subtypes;
          this.subtype = routeName;
        });
    }
  };

  typeSelection(value) {
    this.subtype = undefined;
    this.subtypes = this.applications
      .filter(application => value === application.value)
      .map(application => application.subtypes)
      .shift();
  };

  eventSelection(value) {
    this.router.navigate(['/Applications/' + value]);
  };
}
