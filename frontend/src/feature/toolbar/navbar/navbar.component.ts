import {Component} from '@angular/core';
import {CurrentUser} from '../../../service/user/current-user';
import {AuthService} from '../../login/auth.service';

@Component({
  selector: 'navbar',
  template: require('./navbar.component.html'),
  styles: [
    require('./navbar.component.scss')
  ]
})
export class NavbarComponent {
  authenticated: () => boolean;

  constructor(authService: AuthService) {
    this.authenticated = () => authService.authenticated();
  }

  hasRole(role: string): boolean {
    return CurrentUser.hasRole(role);
  }
}
