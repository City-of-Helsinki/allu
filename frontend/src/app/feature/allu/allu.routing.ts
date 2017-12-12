import {Routes} from '@angular/router';

import {MapSearchComponent} from '../mapsearch/mapsearch.component';
import {WorkQueueComponent} from '../../feature/workqueue/workqueue.component';
import {WorkQueueComponent as SupervisionWorkqueueComponent} from '../../feature/supervision-workqueue/workqueue.component';
import {LoginComponent} from '../../feature/login/login.component';
import {AuthGuard} from '../../service/authorization/auth-guard.service';
import {HandlerModalComponent} from '../common/handlerModal/handler-modal.component';
import {Oauth2Component} from '../oauth2/oauth2.component';
import {CanActivateLogin} from '../../service/authorization/can-activate-login';

export const rootRoutes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'home', component: MapSearchComponent, canActivate: [AuthGuard]},
  { path: 'workqueue', component: WorkQueueComponent, canActivate: [AuthGuard], children: [
    { path: 'handler', component: HandlerModalComponent, canActivate: [AuthGuard] }
  ]},
  { path: 'supervision-tasks', component: SupervisionWorkqueueComponent, canActivate: [AuthGuard], children: [
    { path: 'handler', component: HandlerModalComponent, canActivate: [AuthGuard] }
  ]},
  { path: 'login', component: LoginComponent, canActivate: [CanActivateLogin] },
  { path: 'logout', component: LoginComponent },
  { path: 'oauth2', component: Oauth2Component, canActivate: [AuthGuard]},
  { path: '**', redirectTo: 'home' }
];
