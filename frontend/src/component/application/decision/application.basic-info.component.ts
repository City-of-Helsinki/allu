import {Component, Input} from '@angular/core';

import {MD_CARD_DIRECTIVES} from '@angular2-material/card';

import {Application} from '../../../model/application/application';
import {UI_DATE_FORMAT} from '../../../util/time.util';

@Component({
  selector: 'application-basic-info',
  moduleId: module.id,
  template: require('./application.basic-info.component.html'),
  styles: [],
  directives: [
    MD_CARD_DIRECTIVES
  ]
})
export class ApplicationBasicInfoComponent {
  @Input() application: Application;
  private format = UI_DATE_FORMAT;
}
