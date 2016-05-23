import {Component} from '@angular/core';
import {FORM_DIRECTIVES} from '@angular/common';

import {MD_INPUT_DIRECTIVES} from '@angular2-material/input';
import { MdAnchor, MdButton } from '@angular2-material/button';
import {MD_CARD_DIRECTIVES} from '@angular2-material/card';
import { MdToolbar } from '@angular2-material/toolbar';

import {ToolbarComponent} from '../../component/toolbar/toolbar.component';

import {MapComponent} from '../../component/map/map.component';
import {WorkqueueComponent} from '../../component/workqueue/workqueue.component';

@Component({
  selector: 'application',
  viewProviders: [],
  moduleId: module.id,
  template: require('./application.component.html'),
  styles: [
    require('./application.component.scss')
  ],
  directives: [MD_INPUT_DIRECTIVES, MD_CARD_DIRECTIVES, MdToolbar, MapComponent, WorkqueueComponent]
})

export class ApplicationComponent {
  public application: any;

  constructor() {
    this.application = {
      'name': 'Tapahtuma 1',
      'type': 'Ulkoilmatapahtuma',
      'status': 'Vireillä',
      'information': 'Suspendisse quis arcu dolor. Donec fringilla nunc mollis.',
      'applicant': {
        'companyName': 'Vincit Helsinki Oy',
        'businessID': 'Y-VINCIT',
        'name': 'Sadi Hossain',
        'social': 'XXXXXX',
        'address': 'Mikonkatu 15 A',
        'postalCode': '00100',
        'city': 'Helsinki',
        'phone': '0505291920',
        'email': 'mail@mail.com'
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
      'createDate': '2016-05-18T10:24:06.565+03:00',
      'startDate': undefined,
      'project': {
        'id': undefined,
        'name': 'Hanke1',
        'type': 'Sähkötyö',
        'information': 'Lorem ipsum dolor sit amet, consectetur adipiscing elit.'
      }
    };
  }
}
