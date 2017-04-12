import {Routes} from '@angular/router';
import {AuthGuard} from '../login/auth-guard.service';
import {CustomerListComponent} from './customer-list.component';
import {CustomerComponent} from './customer.component';
import {CustomerRegistryComponent} from './customer-registry.component';

export const customerRegistryRoutes: Routes = [
  { path: 'customers', component: CustomerRegistryComponent, canActivate: [AuthGuard], children: [
    { path: '', component: CustomerListComponent },
    { path: 'new', component: CustomerComponent },
    { path: ':id', component: CustomerComponent }
  ]}
];
