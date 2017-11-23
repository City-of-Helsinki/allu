import {Routes} from '@angular/router';
import {DownloadComponent} from './download.component';
import {AuthGuard} from '../../service/authorization/auth-guard.service';

export const downloadRoutes: Routes = [
  { path: 'download', canActivate: [AuthGuard], children: [
    { path: 'customers/saporder/xlsx', component: DownloadComponent },
    { path: 'customers/saporder/csv', component: DownloadComponent }
  ]}
];
