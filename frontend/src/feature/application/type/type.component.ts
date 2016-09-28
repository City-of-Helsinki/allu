import {Component} from '@angular/core';

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
  template: ''
})

export class TypeComponent {
}
