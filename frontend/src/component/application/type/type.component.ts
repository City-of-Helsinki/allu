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
import {EventService} from '../../../event/event.service';
import {ApplicationSaveEvent} from '../../../event/save/application-save-event';
import {PostalAddress} from '../../../model/common/postal-address';
import {Customer} from '../../../model/common/customer';
import {Applicant} from '../../../model/application/applicant';
import {Person} from '../../../model/common/person';

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

export class TypeComponent {
}
