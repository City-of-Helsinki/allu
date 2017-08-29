import {Component, Input} from '@angular/core';
import {Application} from '../../model/application/application';

@Component({
  selector: 'application-basic-info',
  template: require('./application.basic-info.component.html'),
  styles: [require('./application.basic-info.component.scss')]
})
export class ApplicationBasicInfoComponent {
  @Input() application: Application;
}
