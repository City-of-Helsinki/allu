import {Routes} from '@angular/router';
import {AuthGuard} from '../../service/authorization/auth-guard.service';
import {AdminGuard} from '@app/service/authorization/admin-guard.service';
import {CustomerListComponent} from './customer-list.component';
import {CustomerComponent} from './customer/customer.component';
import {CustomerRegistryComponent} from './customer-registry.component';

export const customerRegistryRoutes: Routes = [
  { path: 'customers', component: CustomerRegistryComponent, canActivate: [AdminGuard], children: [
    { path: '', component: CustomerListComponent },
    { path: 'new', component: CustomerComponent },
    { path: ':id', component: CustomerComponent }
  ]}
];
