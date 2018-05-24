import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {Observable} from 'rxjs';

import {User} from '../../../model/user/user';
import {ExternalUser} from '../../../model/common/external-user';
import {ExternalUserHub} from '../../../service/user/external-user-hub';

@Component({
  selector: 'external-user-list',
  templateUrl: './external-user-list.component.html',
  styleUrls: []
})
export class ExternalUserListComponent implements OnInit {

  users: Observable<Array<ExternalUser>>;

  constructor(private userHub: ExternalUserHub, private router: Router) {}

  ngOnInit(): void {
    this.users = this.userHub.getAllUsers();
  }

  onSelect(user: User): void {
    this.router.navigate(['/admin/external-users', user.id]);
  }

  newUser(): void {
    this.router.navigate(['/admin/external-users/new']);
  }
}
