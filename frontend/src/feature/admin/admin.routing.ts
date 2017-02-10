import {Routes} from '@angular/router';

import {UserListComponent} from '../admin/user/user-list.component';
import {UserComponent} from './user/user.component';
import {AdminComponent} from './admin.component';

export const adminRoutes: Routes = [
  { path: 'admin', component: AdminComponent, children: [
    { path: '', redirectTo: 'user-list', pathMatch: 'full'},
    { path: 'user-list', component: UserListComponent },
    { path: 'user', component: UserComponent },
    { path: 'user/:userName', component: UserComponent }
  ]}
];
