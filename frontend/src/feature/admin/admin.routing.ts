import {Routes} from '@angular/router';

import {UserListComponent} from '../admin/user/user-list.component';
import {UserComponent} from './user/user.component';

export const adminRoutes: Routes = [
  { path: 'admin', redirectTo: 'admin/user-list', pathMatch: 'full' },
  { path: 'admin/user-list', component: UserListComponent },
  { path: 'admin/user/:userName', component: UserComponent }
];
