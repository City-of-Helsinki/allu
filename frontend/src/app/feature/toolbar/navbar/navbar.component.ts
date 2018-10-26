import {Component} from '@angular/core';
import {CurrentUser} from '../../../service/user/current-user';
import {Observable} from 'rxjs';
import {map} from 'rxjs/internal/operators';
import {RoleType} from '@app/model/user/role-type';

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

  hasRole(role: RoleType): Observable<boolean> {
    return this.currentUser.user.pipe(map(u => u.hasRole(role)));
  }
}
