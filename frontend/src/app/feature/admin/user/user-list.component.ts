import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {Observable} from 'rxjs';

import {User} from '@model/user/user';
import {UserService} from '@service/user/user-service';

@Component({
  selector: 'user-list',
  templateUrl: './user-list.component.html',
  styleUrls: []
})
export class UserListComponent implements OnInit {

  users: Observable<Array<User>>;

  constructor(private userService: UserService, private router: Router) {}

  ngOnInit(): void {
    this.users = this.userService.getAllUsers();
  }

  onSelect(user: User): void {
    this.router.navigate(['/admin/users', user.id]);
  }

  newUser(): void {
    this.router.navigate(['/admin/users/new']);
  }
}
