import {Component, Input} from '@angular/core';
import {Application} from '../../model/application/application';

@Component({
  selector: 'application-basic-info',
  templateUrl: './application.basic-info.component.html',
  styleUrls: ['./application.basic-info.component.scss']
})
export class ApplicationBasicInfoComponent {
  @Input() application: Application;
}
