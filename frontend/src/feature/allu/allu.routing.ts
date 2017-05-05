import {Routes} from '@angular/router';

import {MapSearchComponent} from '../mapsearch/mapsearch.component';
import {WorkQueueComponent} from '../../feature/workqueue/workqueue.component';
import {Login} from '../../feature/login/login.component';
import {AuthGuard} from '../../feature/login/auth-guard.service';
import {HandlerModalComponent} from '../workqueue/handlerModal/handler-modal.component';
import {Oauth2Component} from '../oauth2/oauth2.component';


export const rootRoutes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'home', component: MapSearchComponent, canActivate: [AuthGuard]},
  { path: 'workqueue', component: WorkQueueComponent, canActivate: [AuthGuard] },
  { path: 'workqueue/handler', component: HandlerModalComponent, canActivate: [AuthGuard] },
  { path: 'login', component: Login },
  { path: 'logout', component: Login },
  { path: 'oauth2', component: Oauth2Component },
  { path: '**', redirectTo: 'home' }
];
