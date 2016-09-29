import {Component, Input} from '@angular/core';
import {Application} from '../../model/application/application';
import {UI_DATE_FORMAT} from '../../util/time.util';

@Component({
  selector: 'application-basic-info',
  template: require('./application.basic-info.component.html'),
  styles: []
})
export class ApplicationBasicInfoComponent {
  @Input() application: Application;
  private format = UI_DATE_FORMAT;
}
