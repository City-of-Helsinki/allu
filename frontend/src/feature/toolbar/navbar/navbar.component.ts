import {Component} from '@angular/core';
import {CurrentUser} from '../../../service/user/current-user';
import {Observable} from 'rxjs/Observable';

@Component({
  selector: 'navbar',
  template: require('./navbar.component.html'),
  styles: [
    require('./navbar.component.scss')
  ]
})
export class NavbarComponent {
  constructor(private currentUser: CurrentUser) {
  }

  hasRole(role: string): Observable<boolean> {
    return this.currentUser.user.map(u => u.hasRole(role));
  }
}
