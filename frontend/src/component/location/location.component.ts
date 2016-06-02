import {Component} from '@angular/core';
import {FORM_DIRECTIVES} from '@angular/common';
import {RouteParams} from '@angular/router-deprecated';

import {MD_INPUT_DIRECTIVES} from '@angular2-material/input';
import {MdAnchor, MdButton} from '@angular2-material/button';
import {MD_CARD_DIRECTIVES} from '@angular2-material/card';
import {MdToolbar} from '@angular2-material/toolbar';
import {MdRadioButton} from '@angular2-material/radio';
import {MdRadioDispatcher} from '@angular2-material/radio/radio_dispatcher';
import {MdCheckbox} from '@angular2-material/checkbox';

import {MapComponent} from '../map/map.component';

import {ApplicationsAnnounceEvent} from '../../event/announce/applications-announce-event';
import {Event} from '../../event/event';
import {EventListener} from '../../event/event-listener';
import {Application} from '../../model/application/application';
import {Customer} from '../../model/customer/customer';
import {EventService} from '../../event/event.service';
import {ApplicationSaveEvent} from '../../event/save/application-save-event';
import {ApplicationsLoadEvent} from '../../event/load/applications-load-event';
import {ShapeAnnounceEvent} from '../../event/announce/shape-announce-event';

@Component({
  selector: 'type',
  viewProviders: [],
  moduleId: module.id,
  template: require('./location.component.html'),
  styles: [
    require('./location.component.scss')
  ],
  directives: [
    MdToolbar,
    MD_INPUT_DIRECTIVES,
    MD_CARD_DIRECTIVES,
    MdButton,
    MapComponent
  ],
  providers: []
})

export class LocationComponent implements EventListener {
  private application: Application;
  private id: number;
  private features: GeoJSON.FeatureCollection<GeoJSON.GeometryObject>;


  constructor(private eventService: EventService, params: RouteParams) {
    this.id = Number(params.get('id'));
  };

  public handle(event: Event): void {
    // ShapeAnnounceEvent
    if (event instanceof ShapeAnnounceEvent) {
      this.features = event.shape;
      console.log('JEE!');
    } else if (event instanceof ApplicationsAnnounceEvent) {
      let aaEvent = <ApplicationsAnnounceEvent> event;
      let applications = aaEvent.applications;
      this.application = applications.filter((a) => a.id === 8)[0];
    }
  }


  save() {
    console.log('Saving location for application id: ', this.id);
    this.application.area = this.features;
    this.application.name = 'Foobar';
    let saveEvent = new ApplicationSaveEvent(this.application);
    this.eventService.send(this, saveEvent);
    localStorage.setItem('application', JSON.stringify(this.features));

  }

  ngOnInit() {
    this.eventService.subscribe(this);
    this.eventService.send(this, new ApplicationsLoadEvent('Minna'));
  }

  ngOnDestroy() {
    this.eventService.unsubscribe(this);
  }
}
