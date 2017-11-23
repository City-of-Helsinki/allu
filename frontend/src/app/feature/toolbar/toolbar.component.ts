import {Component} from '@angular/core';
import {AuthService} from '../../service/authorization/auth.service';

@Component({
  selector: 'toolbar',
  templateUrl: './toolbar.component.html',
  styleUrls: [
    './toolbar.component.scss'
  ]
})
export class ToolbarComponent {
  constructor(private authService: AuthService) {
  }

  get authenticated() {
    return this.authService.authenticated();
  }
}
