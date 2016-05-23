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
  public application: any

  constructor() {
    this.application = {
      "name": "Tapahtuma 1",
      "type": "Ulkoilmatapahtuma",
      "status": "Vireillä",
      "information": "Suspendisse quis arcu dolor. Donec fringilla nunc mollis aliquet mollis. Donec commodo tempus erat. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Duis convallis sem tincidunt enim mattis eleifend eget eu ante",
      "applicant": {
        "companyName": 'Vincit Helsinki Oy',
        "businessID": 'Y-VINCIT',
        "name": 'Sadi Hossain',
        "social": 'XXXXXX',
        "address": 'Mikonkatu 15 A',
        "postalCode": '00100',
        "city": 'Helsinki',
        "phone": '0505291920',
        "email": 'sadi.hossain@vincit.fi'
      },
      "contact": {
        "name": 'Jan Nikander',
        "social": 'XXYYZZ',
        "address": 'Mikonkatu 15 A',
        "postalCode": '00100',
        "city": 'Helsinki',
        "phone": '0505291920',
        "email": 'jan.nikander@vincit.fi'
      },
      "customer": {
        "id": null,
        "type": "Henkilöasiakas",
        "address": "Jokutie",
        "postOffice": "HELSINKI",
        "zipCode": "00100",
        "district": null,
        "email": "mail@mail.com"
      },
      "createDate": "2016-05-18T10:24:06.565+03:00",
      "startDate": null,
      "project": {
        "id": null,
        "name": "Hanke1",
        "type": "Sähkötyö",
        "information": "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec quis congue erat. Aenean eget suscipit neque. Quisque et tincidunt dui. Donec dictum tellus lectus, ut lobortis nulla mollis nec. Morbi ante est, tristique eu eros ut, cursus consectetur justo. Donec varius sodales arcu, a posuere velit porta quis. Aliquam erat volutpat. Aliquam bibendum in lectus ac ornare. Aenean lacus massa, maximus et metus eu, rutrum bibendum massa."
      }
    };
  }
}
