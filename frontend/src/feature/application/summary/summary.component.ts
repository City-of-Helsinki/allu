import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {ApplicationsAnnounceEvent} from '../../../event/announce/applications-announce-event';
import {ApplicationSelectionEvent} from '../../../event/selection/application-selection-event';

import {MapUtil} from '../../../service/map.util.ts';
import {ProgressStep, ProgressMode} from '../../../feature/progressbar/progressbar.component';

import {Event} from '../../../event/event';
import {EventListener} from '../../../event/event-listener';
import {Application} from '../../../model/application/application';
import {EventService} from '../../../event/event.service';
import {ApplicationSaveEvent} from '../../../event/save/application-save-event';
import {ApplicationAddedAnnounceEvent} from '../../../event/announce/application-added-announce-event';
import {ApplicationLoadFilter} from '../../../event/load/application-load-filter';
import {ApplicationsLoadEvent} from '../../../event/load/applications-load-event';
import {ApplicationHub} from '../../../service/application/application-hub';
import {MapHub} from '../../../service/map-hub';
import {Subscription} from 'rxjs/Subscription';

@Component({
  selector: 'summary',
  viewProviders: [],
  template: require('./summary.component.html'),
  styles: [
    require('./summary.component.scss')
  ]
})

export class SummaryComponent implements EventListener, OnInit, OnDestroy {
  private application: Application;
  private id: number;
  private events: Array<any>;
  private applicantType: any;
  private applicantText: any;
  private countries: Array<any>;
  private billingTypes: Array<any>;
  private noPriceReasons: Array<any>;

  private rentingPlace: any;
  private sections: any;
  private area: number;

  private progressStep: number;
  private progressMode: number;

  private applicationSubscription: Subscription;

  constructor(private eventService: EventService, private applicationHub: ApplicationHub, private mapHub: MapHub,
              private route: ActivatedRoute) {
    this.events = [
      {name: 'Ulkoilmatapahtuma', value: 'OutdoorEvent'},
      {name: 'Muu', value: 'Other'}
    ];
    this.applicantType = {'COMPANY': 'Yritys', 'ASSOCIATION': 'Yhdistys', 'PERSON': 'Yksityishenkilö'};
    this.applicantText = {
      'DEFAULT': {
        name: 'Hakijan nimi',
        id: 'Y-tunnus'},
      'COMPANY': {
        name: 'Yrityksen nimi',
        id: 'Y-tunnus'},
      'ASSOCIATION': {
        name: 'Yhdistyksen nimi',
        id: 'Y-tunnus'},
      'PERSON': {
        name: 'Henkilön nimi',
        id: 'Henkilötunnus'}
    };
    this.countries = [
      {name: 'Suomi', value: 'Finland'},
      {name: 'Ruotsi', value: 'Sweden'},
      {name: 'Venäjä', value: 'Russia'},
      {name: 'Viro', value: 'Estonia'}
    ];
    this.billingTypes = [
      {name: 'Käteinen', value: 'Cash'},
      {name: 'Lasku', value: 'Invoice'},
      {name: 'Suoravelotus', value: 'BankTransaction'}
    ];
    this.noPriceReasons = [
      {name: 'Hyväntekeväisyys- tai kansalaisjärjestö tai oppilaistoksen tapahtuma', value: 'Charity'},
      {name: 'Taide- tai kulttuuritapahtuma', value: 'ArtOrCulture'},
      {name: 'Avoin ja maksuton urheilutapahtuma', value: 'NoFeeSporting'},
      {name: 'Asukas- tai kaupunginosayhdistyksen tapahtuma', value: 'ResidentOrCity'},
      {name: 'Aatteellinen, hengellinen tai yhteiskunnallinen tapahtuma', value: 'Spiritual'},
      {name: 'Kaupunki isäntäjä tai järjestäjäkumppanina', value: 'City'},
      {name: 'Tilataideteos', value: 'Art'},
      {name: 'Nuorisojärjestön tapahtuma', value: 'Youth'},
      {name: 'Yksityishenkilön järjestämä merkkipäiväjuhla tai vastaava', value: 'PrivateFunction'},
      {name: 'Puolustus- tai poliisivoimien tapahtuma', value: 'DefenceOrPolice'}
    ];

    this.rentingPlace = 'Narinkkatori';
    this.sections = 'D-lohko';
    this.area = 300;

    this.progressStep = ProgressStep.SUMMARY;
    this.progressMode = ProgressMode.NEW;
  };

  ngOnInit(): any {
    this.route.params.subscribe(params => {
      this.id = Number(params['id']);
    });

    this.eventService.subscribe(this);
    let filter = new ApplicationLoadFilter();
    filter.applicationId = this.id;
    this.applicationSubscription = this.applicationHub.applications().subscribe(applications => this.handleApplications(applications));
    this.applicationHub.addApplicationSearch(this.id);
  }

  ngOnDestroy(): any {
    this.eventService.unsubscribe(this);
    this.applicationSubscription.unsubscribe();
  }

  public handle(event: Event): void {
  }

  save(application: any) {
    // Save application
    console.log('Saving application', application);
    let saveEvent = new ApplicationSaveEvent(application);
    this.eventService.send(this, saveEvent);
   }

  private handleApplications(applications: Array<Application>): void {
    this.application = applications.find(app => app.id === this.id);
    if (this.application !== undefined) {
      this.mapHub.addApplicationSelection(this.application);
    }
  }
}
