import {Component, Input} from '@angular/core';
import {Application} from '../../model/application/application';
import {UI_PIPE_DATE_FORMAT} from '../../util/time.util';
import {translations} from '../../util/translations';

@Component({
  selector: 'application-basic-info',
  template: require('./application.basic-info.component.html'),
  styles: [require('./application.basic-info.component.scss')]
})
export class ApplicationBasicInfoComponent {
  @Input() application: Application;
  translations = translations;
  private format = UI_PIPE_DATE_FORMAT;
}
