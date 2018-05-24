import {Component} from '@angular/core';
import {CurrentUser} from '../../../service/user/current-user';
import {Observable} from 'rxjs';
import {map} from 'rxjs/internal/operators';

@Component({
  selector: 'navbar',
  templateUrl: './navbar.component.html',
  styleUrls: [
    './navbar.component.scss'
  ]
})
export class NavbarComponent {
  constructor(private currentUser: CurrentUser) {
  }

  hasRole(role: string): Observable<boolean> {
    return this.currentUser.user.pipe(map(u => u.hasRole(role)));
  }
}
