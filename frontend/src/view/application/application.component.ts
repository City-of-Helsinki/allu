import {Component} from '@angular/core';
import {FORM_DIRECTIVES} from '@angular/common';

import {MD_INPUT_DIRECTIVES} from '@angular2-material/input';
import { MdAnchor, MdButton } from '@angular2-material/button';
import {MD_CARD_DIRECTIVES} from '@angular2-material/card';
import { MdToolbar } from '@angular2-material/toolbar';

import {ToolbarComponent} from '../../component/toolbar/toolbar.component';

import {MapComponent} from '../../component/map/map.component';
import {WorkqueueComponent} from '../../component/workqueue/workqueue.component';

import {WorkqueueService} from '../../service/workqueue.service';

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
  public workqueue: WorkqueueService;

  constructor(workqueue: WorkqueueService) {
    this.workqueue = workqueue;
    this.application = {
      'id': 12121,
      'title': 'Fenniakortteli julkisivuremontti',
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
      'latitude': 60.17157405145976,
      'longitude': 24.94623363018036,
      'createDate': '2016-05-18T10:24:06.565+03:00',
      'startDate': undefined,
      'project': {
        'id': undefined,
        'name': 'Hanke1',
        'type': 'Sähkötyö',
        'information': 'Lorem ipsum dolor sit amet, consectetur adipiscing elit.'
      }
    };
  };

  save(application: any) {
      // Save application
      console.log(application);
      this.workqueue.add(application);
      console.log(this.workqueue.getAll());

   }
}
