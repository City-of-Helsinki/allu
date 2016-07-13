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


@Component({
  selector: 'promotion-event',
  viewProviders: [],
  moduleId: module.id,
  template: require('./promotion-event.component.html'),
  styles: [
    require('./promotion-event.component.scss')
  ],
  directives: [
    ROUTER_DIRECTIVES,
    MaterializeDirective,
    MD_INPUT_DIRECTIVES,
    MD_CARD_DIRECTIVES,
    MdToolbar,
    MdButton,
    MdRadioButton,
    MdCheckbox
  ],
  providers: [MdRadioDispatcher]
})

export class PromotionEventComponent implements EventListener, OnInit, OnDestroy {
  private application: Application;
  private events: Array<any>;
  private applicantType: Array<string>;
  private countries: Array<any>;
  private billingTypes: Array<any>;
  private noPriceReasons: Array<any>;


  constructor(private eventService: EventService) {
    // this.application = Application.emptyApplication();
    this.application = Application.preFilledApplication();
  };

  ngOnInit(): any {
    this.eventService.subscribe(this);
  }

  ngOnDestroy(): any {
    this.eventService.unsubscribe(this);
  }

  public handle(event: Event): void {
    if (event instanceof ApplicationAddedAnnounceEvent) {
      let aaaEvent = <ApplicationAddedAnnounceEvent>event;
      console.log('Successfully added new application', aaaEvent.application);
    }
  }
}
