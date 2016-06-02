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
  selector: 'outdoor-event',
  viewProviders: [],
  moduleId: module.id,
  template: require('./outdoor-event.component.html'),
  styles: [
    require('./outdoor-event.component.scss')
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

export class OutdoorEventComponent implements EventListener {
  public application: any;
  public workqueue: WorkqueueService;

  constructor(private eventService: EventService) {
    this.application = {
      'id': 12121,
      'title': 'Fenniakortteli julkisivuremontti',
      'type': 'Ulkoilmatapahtuma',
      'status': 'Vireillä',
      'description': 'Suspendisse quis arcu dolor. Donec fringilla nunc mollis.',
      'applicant': {
        'companyName': 'Vincit Helsinki Oy',
        'businessID': 'Y-VINCIT',
        'name': 'Sadi Hossain',
        'social': 'XXXXXX',
        'address': 'Mikonkatu 15 A',
        'postalCode': '00100',
        'city': 'Helsinki',
        'phone': '0501234567',
        'email': 'mail@mail.com'
      },
      'billing': {
        'type': 'Paperilasku',
        'workNumber': 12121,
        'reference': 5315,
        'address': 'Mikonkatu 15 A',
        'postalCode': '00100',
        'city': 'Helsinki',
        'sales': true
      },
      'contact': {
        'name': 'Jan Nikander',
        'social': 'XXYYZZ',
        'address': 'Mikonkatu 15 A',
        'postalCode': '00100',
        'city': 'Helsinki',
        'phone': '0505291920',
        'email': 'mail@mail.com'
      },
      'customer': {
        'id': undefined,
        'type': 'Henkilöasiakas',
        'address': 'Jokutie',
        'postOffice': 'HELSINKI',
        'zipCode': '00100',
        'district': undefined,
        'email': 'mail@mail.com'
      },
      'area': {
        'type': 'polygon',
        'latlngs': [
          {'lat': 60.171976960061016, 'lng': 24.945332407951355},
          {'lat': 60.17092298340843, 'lng': 24.94544506072998},
          {'lat': 60.17092565174643, 'lng': 24.945541620254517},
          {'lat': 60.17090697337575, 'lng': 24.945541620254517},
          {'lat': 60.17090964171508, 'lng': 24.945627450942993},
          {'lat': 60.17103238508943, 'lng': 24.946051239967343},
          {'lat': 60.17135791964392, 'lng': 24.94696855545044},
          {'lat': 60.17141662225308, 'lng': 24.9471241235733},
          {'lat': 60.17146998817038, 'lng': 24.947209954261776},
          {'lat': 60.17154470030895, 'lng': 24.947285056114193},
          {'lat': 60.171659436476645, 'lng': 24.947327971458435},
          {'lat': 60.17177950878116, 'lng': 24.947327971458435},
          {'lat': 60.17202498857124, 'lng': 24.947301149368286}
        ]
      },
      'structure': {
        'size': 15.24,
        'description': 'Lorem ipsum dolor sit amet, consectetur adipiscing elit.',
        'startDate': '10.06.2016',
        'endDate': '17.06.2016'
      },
      'latitude': 60.17157405145976,
      'longitude': 24.94623363018036,
      'createDate': '2016-05-18T10:24:06.565+03:00',
      'startDate': '12.06.2016',
      'endDate': '15.06.2016',
      'project': {
        'id': undefined,
        'name': 'Hanke1',
        'type': 'Sähkötyö',
        'information': 'Lorem ipsum dolor sit amet, consectetur adipiscing elit.'
      }
    };
  };

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
