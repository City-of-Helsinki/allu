import {Component, OnDestroy, OnInit} from '@angular/core';
import {FORM_DIRECTIVES} from '@angular/common';
import {RouteParams, ROUTER_DIRECTIVES} from '@angular/router-deprecated';

import {MD_INPUT_DIRECTIVES} from '@angular2-material/input';
import {MdAnchor, MdButton} from '@angular2-material/button';
import {MD_CARD_DIRECTIVES} from '@angular2-material/card';
import {MdToolbar} from '@angular2-material/toolbar';
import {MdRadioButton} from '@angular2-material/radio';
import {MdRadioDispatcher} from '@angular2-material/radio/radio_dispatcher';
import {MdCheckbox} from '@angular2-material/checkbox';

import {MaterializeDirective} from 'angular2-materialize';

import {ApplicationsAnnounceEvent} from '../../../event/announce/applications-announce-event';
import {ApplicationSelectionEvent} from '../../../event/selection/application-selection-event';

import {MapUtil} from '../../../service/map.util.ts';
import {MapComponent} from '../../map/map.component';
import {ProgressStep, ProgressMode, ProgressbarComponent} from '../../../component/progressbar/progressbar.component';

import {Event} from '../../../event/event';
import {EventListener} from '../../../event/event-listener';
import {Application} from '../../../model/application/application';
import {EventService} from '../../../event/event.service';
import {ApplicationSaveEvent} from '../../../event/save/application-save-event';
import {ApplicationAddedAnnounceEvent} from '../../../event/announce/application-added-announce-event';
import {ApplicationLoadFilter} from '../../../event/load/application-load-filter';
import {ApplicationsLoadEvent} from '../../../event/load/applications-load-event';
import {ApplicationAttachmentComponent} from '../attachment/application-attachment.component';


@Component({
  selector: 'summary',
  viewProviders: [],
  moduleId: module.id,
  template: require('./summary.component.html'),
  styles: [
    require('./summary.component.scss')
  ],
  directives: [
    ROUTER_DIRECTIVES,
    MaterializeDirective,
    MD_INPUT_DIRECTIVES,
    MD_CARD_DIRECTIVES,
    MdToolbar,
    MdButton,
    MdRadioButton,
    MdCheckbox,
    MapComponent,
    ProgressbarComponent,
    ApplicationAttachmentComponent
  ],
  providers: [MdRadioDispatcher]
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



  constructor(private eventService: EventService, params: RouteParams) {
    this.id = Number(params.get('id'));
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
    this.eventService.subscribe(this);
    let filter = new ApplicationLoadFilter();
    filter.applicationId = this.id;
    this.eventService.send(this, new ApplicationsLoadEvent(filter));
  }

  ngOnDestroy(): any {
    this.eventService.unsubscribe(this);
  }

  public handle(event: Event): void {
    if (event instanceof ApplicationsAnnounceEvent) {
      let aaEvent = <ApplicationsAnnounceEvent> event;
      // we're only interested about applications matching the application being summarized

      if (aaEvent.applications.length === 1 && aaEvent.applications[0].id === this.id) {
        this.application = aaEvent.applications[0];
        this.eventService.send(this, new ApplicationSelectionEvent(this.application));
      }
    }
  }

  eventTypeSelection(value: string) {
    console.log('Tapahtuman tyypiksi on valittu: ', value);

  }

  applicantTypeSelection(value: string) {
    console.log('Hakijan tyypiksi on valittu: ', value);
    console.log(this.application);
  }

  applicantCountrySelection(value: string) {
    console.log('Hakijan maaksi on valittu: ', value);
  }

  saveToRegistry(value: string) {
    console.log('Hakijan maaksi on valittu: ', value);
  }

  newContact(value: string) {
    console.log('Uudeksi yhteyshenkilöksi on valittu: ', value);
  }

  billingTypeSelection(value: string) {
    console.log('Laskutustavaksi on valittu: ', value);
  }

  billingCountrySelection(value: string) {
    console.log('Hakijan maaksi on valittu: ', value);
  }

  save(application: any) {
    // Save application
    console.log('Saving application', application);
    let saveEvent = new ApplicationSaveEvent(application);
    this.eventService.send(this, saveEvent);
   }
}
