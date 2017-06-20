import {Component} from '@angular/core';
import {CurrentUser} from '../../../service/user/current-user';
import {AuthService} from '../../../service/authorization/auth.service';
import {Observable} from 'rxjs/Observable';

@Component({
  selector: 'navbar',
  template: require('./navbar.component.html'),
  styles: [
    require('./navbar.component.scss')
  ]
})
export class NavbarComponent {
  authenticated: () => boolean;

  constructor(authService: AuthService, private currentUser: CurrentUser) {
    this.authenticated = () => authService.authenticated();
  }

  hasRole(role: string): Observable<boolean> {
    return this.currentUser.user.map(u => u.hasRole(role));
  }
}
