import {Component} from '@angular/core';
import {CurrentUser} from '../../../service/user/current-user';

@Component({
  selector: 'navbar',
  template: require('./navbar.component.html'),
  styles: [
    require('./navbar.component.scss')
  ]
})
export class NavbarComponent {
  hasRole(role: string): boolean {
    return CurrentUser.hasRole(role);
  }
}
