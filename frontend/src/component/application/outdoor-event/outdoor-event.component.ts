import {Component, OnDestroy, OnInit} from '@angular/core';
import {FORM_DIRECTIVES} from '@angular/common';
import {ROUTER_DIRECTIVES} from '@angular/router-deprecated';

import {MD_INPUT_DIRECTIVES} from '@angular2-material/input';
import {MdAnchor, MdButton} from '@angular2-material/button';
import {MD_CARD_DIRECTIVES} from '@angular2-material/card';
import {MdToolbar} from '@angular2-material/toolbar';
import {MdRadioButton} from '@angular2-material/radio';
import {MdRadioDispatcher} from '@angular2-material/radio/radio_dispatcher';
import {MdCheckbox} from '@angular2-material/checkbox';

import {MaterializeDirective} from 'angular2-materialize';

import {Event} from '../../../event/event';
import {EventListener} from '../../../event/event-listener';
import {Application} from '../../../model/application/application';
import {EventService} from '../../../event/event.service';
import {ApplicationSaveEvent} from '../../../event/save/application-save-event';
import {ApplicationAddedAnnounceEvent} from '../../../event/announce/application-added-announce-event';
import {StructureMeta} from '../../../model/application/structure-meta';
import {MetaLoadEvent} from '../../../event/load/meta-load-event';
import {MetaAnnounceEvent} from '../../../event/announce/meta-announce-event';
import {LoadingComponent} from '../../loading/loading.component';
import {TimeUtil, PICKADATE_PARAMETERS} from '../../../util/time.util';


@Component({
  selector: 'outdoor-event',
  viewProviders: [],
  moduleId: module.id,
  template: require('./outdoor-event.component.html'),
  styles: [
    require('./outdoor-event.component.scss')
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
    LoadingComponent
  ],
  providers: [MdRadioDispatcher]
})

export class OutdoorEventComponent implements EventListener, OnInit, OnDestroy {
  private application: Application;
  private events: Array<any>;
  private applicantType: Array<string>;
  private countries: Array<any>;
  private billingTypes: Array<any>;
  private noPriceReasons: Array<any>;
  private pickadateParams = PICKADATE_PARAMETERS;

  private meta: StructureMeta;

  constructor(private eventService: EventService) {
    // this.application = Application.emptyApplication();
    this.application = Application.preFilledApplication();

    this.events = [
      {name: 'Ulkoilmatapahtuma', value: 'OutdoorEvent'},
      {name: 'Muu', value: 'Other'}
    ];
    this.applicantType = ['Yritys', 'Yhdistys', 'Yksityishenkilö'];
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
  };

  ngOnInit(): any {
    this.eventService.subscribe(this);
    this.eventService.send(this, new MetaLoadEvent('OUTDOOREVENT'));
  }

  ngOnDestroy(): any {
    this.eventService.unsubscribe(this);
  }

  public handle(event: Event): void {
    if (event instanceof ApplicationAddedAnnounceEvent) {
      let aaaEvent = <ApplicationAddedAnnounceEvent>event;
      console.log('Successfully added new application', aaaEvent.application);
      this.application = aaaEvent.application;
    } else if (event instanceof MetaAnnounceEvent) {
      console.log('Loaded metadata', event);
      let maEvent = <MetaAnnounceEvent>event;
      this.meta = maEvent.structureMeta;
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

  save(application: Application) {
    // Save application
    console.log('Saving application', application);
    application.metadata = this.meta;
    let saveEvent = new ApplicationSaveEvent(application);
    this.eventService.send(this, saveEvent);
   }
}
