import {Routes} from '@angular/router';

import {MapSearchComponent} from '../mapsearch/mapsearch.component';
import {WorkQueueComponent} from '../../feature/workqueue/workqueue.component';
import {WorkQueueComponent as SupervisionWorkqueueComponent} from '../../feature/supervision-workqueue/workqueue.component';
import {LoginComponent} from '../../feature/login/login.component';
import {AuthGuard} from '../../service/authorization/auth-guard.service';
import {OwnerModalComponent} from '../common/ownerModal/owner-modal.component';
import {Oauth2Component} from '../oauth2/oauth2.component';
import {CanActivateLogin} from '../../service/authorization/can-activate-login';
import {WorkQueueContentComponent} from '../workqueue/content/workqueue-content.component';
import {
  WorkQueueContentComponent as SupervisionWorkQueueContentComponent
} from '../supervision-workqueue/content/workqueue-content.component';
import {ErrorPageComponent} from '@feature/common/error-page/error-page.component';

export const rootRoutes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'home', component: MapSearchComponent, canActivate: [AuthGuard]},
  { path: 'workqueue', component: WorkQueueComponent, canActivate: [AuthGuard], children: [
    { path: '', redirectTo: 'own', pathMatch: 'full'},
    { path: 'owner', component: OwnerModalComponent, canActivate: [AuthGuard] },
    { path: 'own', component: WorkQueueContentComponent, canActivate: [AuthGuard], data: {tab: 'OWN'}},
    { path: 'common', component: WorkQueueContentComponent, canActivate: [AuthGuard], data: {tab: 'COMMON'}}
  ]},
  { path: 'supervision-tasks', component: SupervisionWorkqueueComponent, canActivate: [AuthGuard], children: [
    { path: '', redirectTo: 'own', pathMatch: 'full'},
    { path: 'own', component: SupervisionWorkQueueContentComponent, canActivate: [AuthGuard], data: {tab: 'OWN'}},
    { path: 'common', component: SupervisionWorkQueueContentComponent, canActivate: [AuthGuard], data: {tab: 'COMMON'}},
    { path: 'owner', component: OwnerModalComponent, canActivate: [AuthGuard] },
  ]},
  { path: 'login', component: LoginComponent, canActivate: [CanActivateLogin] },
  { path: 'logout', component: LoginComponent },
  { path: 'oauth2', component: Oauth2Component, canActivate: [AuthGuard]},
  { path: 'error', component: ErrorPageComponent },
  { path: '**', redirectTo: 'home' }
];
