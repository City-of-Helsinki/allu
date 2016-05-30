import {Component} from '@angular/core';
import {FORM_DIRECTIVES} from '@angular/common';

import {MD_INPUT_DIRECTIVES} from '@angular2-material/input';
import {MdAnchor, MdButton} from '@angular2-material/button';
import {MD_CARD_DIRECTIVES} from '@angular2-material/card';
import {MdToolbar} from '@angular2-material/toolbar';
import {MdRadioButton} from '@angular2-material/radio';
import {MdRadioDispatcher} from '@angular2-material/radio/radio_dispatcher';
import {MdCheckbox} from '@angular2-material/checkbox';

import {MapComponent} from '../../map/map.component';

import {WorkqueueService} from '../../../service/workqueue.service';
import {ApplicationsAnnounceEvent} from '../../../event/announce/applications-announce-event';
import {Event} from '../../../event/event';
import {EventListener} from '../../../event/event-listener';
import {Application} from '../../../model/application/application';
import {Customer} from '../../../model/customer/customer';
import {EventService} from '../../../event/event.service';
import {ApplicationSaveEvent} from '../../../event/save/application-save-event';

@Component({
  selector: 'type',
  viewProviders: [],
  moduleId: module.id,
  template: require('./location.component.html'),
  styles: [
    require('./location.component.scss')
  ],
  directives: [
    MD_INPUT_DIRECTIVES,
    MD_CARD_DIRECTIVES,
    MdButton,
    MapComponent
  ],
  providers: []
})

export class LocationComponent implements EventListener {
  public application: any;
  public workqueue: WorkqueueService;

  constructor(private eventService: EventService) {
  };

  public handle(event: Event): void {
    if (event instanceof ApplicationsAnnounceEvent) {
      alert('Application stored!');
    }
  }


  save(application: any) {
    // Save application
    console.log('Saving application', application);
    let customer = new Customer(application.applicant.name);
    let newApplication = new Application(undefined, 'uusi hakemus', 'uusi hakemus', 'tyyppi', 'aika', 1, 1, undefined, customer);
    let saveEvent = new ApplicationSaveEvent(newApplication);
    this.eventService.send(this, saveEvent);

      // console.log(application);
      // this.workqueue.add(application);
      // console.log(this.workqueue.getAll());

   }
}
