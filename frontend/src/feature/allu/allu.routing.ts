import {Routes} from '@angular/router';

import {MapSearchComponent} from '../mapsearch/mapsearch.component';
import {WorkQueueComponent} from '../../feature/workqueue/workqueue.component';
import {DecisionComponent} from '../../feature/decision/decision.component';
import {SearchComponent} from '../../feature/search/search.component';
import {Login} from '../../feature/login/login.component';
import {AuthGuard} from '../../feature/login/auth-guard.service';
import {HandlerModalComponent} from '../workqueue/handlerModal/handler-modal.component';


export const rootRoutes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'home', component: MapSearchComponent, canActivate: [AuthGuard]},
  { path: 'workqueue', component: WorkQueueComponent, canActivate: [AuthGuard] },
  { path: 'workqueue/handler', component: HandlerModalComponent, canActivate: [AuthGuard] },
  { path: 'decision/:id', component: DecisionComponent, canActivate: [AuthGuard] },
  { path: 'login', component: Login },
  { path: 'logout', component: Login },
  { path: '**', redirectTo: 'home' }
];
