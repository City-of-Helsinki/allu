import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';

import {UserHub} from '../../../service/user/user-hub';
import {User} from '../../../model/user/user';

@Component({
  selector: 'user-list',
  templateUrl: './user-list.component.html',
  styleUrls: []
})
export class UserListComponent implements OnInit {

  users: Observable<Array<User>>;

  constructor(private userHub: UserHub, private router: Router) {}

  ngOnInit(): void {
    this.users = this.userHub.getAllUsers();
  }

  onSelect(user: User): void {
    this.router.navigate(['/admin/users', user.id]);
  }

  newUser(): void {
    this.router.navigate(['/admin/users/new']);
  }
}
