import {Component} from '@angular/core';
import {AuthService} from '../../service/authorization/auth.service';

@Component({
  selector: 'toolbar',
  template: require('./toolbar.component.html'),
  styles: [
    require('./toolbar.component.scss')
  ]
})
export class ToolbarComponent {
  constructor(private authService: AuthService) {
  }

  get authenticated() {
    return this.authService.authenticated();
  }
}
