import {Component} from '@angular/core';
import {FORM_DIRECTIVES} from '@angular/common';
import {ROUTER_DIRECTIVES} from '@angular/router-deprecated';

import {MD_INPUT_DIRECTIVES} from '@angular2-material/input';
import {MdAnchor, MdButton} from '@angular2-material/button';
import {MD_CARD_DIRECTIVES} from '@angular2-material/card';
import {MdToolbar} from '@angular2-material/toolbar';
import {MdRadioButton} from '@angular2-material/radio';
import {MdRadioDispatcher} from '@angular2-material/radio/radio_dispatcher';
import {MdCheckbox} from '@angular2-material/checkbox';

import {WorkqueueService} from '../../../service/workqueue.service';
import {ApplicationsAnnounceEvent} from '../../../event/announce/applications-announce-event';
import {Event} from '../../../event/event';
import {EventListener} from '../../../event/event-listener';
import {Application} from '../../../model/application/application';
import {Customer} from '../../../model/customer/customer';
import {EventService} from '../../../event/event.service';
import {ApplicationSaveEvent} from '../../../event/save/application-save-event';
import {PostalAddress} from '../../../model/common/postal-address';

@Component({
  selector: 'type',
  viewProviders: [],
  moduleId: module.id,
  template: require('./type.component.html'),
  styles: [
    require('./type.component.scss')
  ],
  directives: [
    ROUTER_DIRECTIVES,
    MD_INPUT_DIRECTIVES,
    MD_CARD_DIRECTIVES,
    MdToolbar,
    MdButton,
    MdRadioButton,
    MdCheckbox
  ],
  providers: [MdRadioDispatcher]
})

export class TypeComponent implements EventListener {
  public application: any;
  public workqueue: WorkqueueService;

  constructor(private eventService: EventService) {
  }

  public handle(event: Event): void {
    if (event instanceof ApplicationsAnnounceEvent) {
      alert('Application stored!');
    }
  }

  save(application: any) {
    // Save application
    console.log('Saving application', application);
    let postalAddress =
      new PostalAddress(application.customer.address, application.customer.zipCode, undefined);
    // TODO: applicant is not the customer? Or is it?
    let customer =
      new Customer(
        undefined,
        application.applicant.name,
        application.customer.type,
        postalAddress,
        application.customer.email,
        undefined);
    console.log('Mapped customer', customer);
    let newApplication =
      new Application(
        undefined, // application.id,
        application.title,
        application.type,
        application.status,
        1,
        1,
        undefined,
        customer,
        undefined,
        undefined,
        undefined,
        undefined);
    let saveEvent = new ApplicationSaveEvent(newApplication);
    this.eventService.send(this, saveEvent);

      // console.log(application);
      // this.workqueue.add(application);
      // console.log(this.workqueue.getAll());

   }
}
