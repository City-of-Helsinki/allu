import {Routes} from '@angular/router';

import {MapSearchComponent} from '../mapsearch/mapsearch.component';
import {ApplicationComponent} from '../application/application.component';
import {WorkQueueComponent} from '../../component/workqueue/workqueue.component';
import {LocationComponent} from '../../component/location/location.component';
import {SummaryComponent} from '../../component/application/summary/summary.component';
import {DecisionComponent} from '../../component/application/decision/decision.component';
import {SearchComponent} from '../../component/search/search.component';
import {Login} from '../../component/login/login.component';
import {OutdoorEventComponent} from '../../component/application/outdoor-event/outdoor-event.component';
import {PromotionEventComponent} from '../../component/application/promotion-event/promotion-event.component';
import {TypeComponent} from '../../component/application/type/type.component';
import {AuthGuard} from '../../component/login/auth-guard.service';

export const rootRoutes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'home', component: MapSearchComponent, canActivate: [AuthGuard]},
  { path: 'applications', component: ApplicationComponent, canActivate: [AuthGuard], children: [
    { path: '', component: TypeComponent, canActivate: [AuthGuard] }, //  useAsDefault: true }, coming soon!
    { path: 'outdoor-event', component: OutdoorEventComponent, canActivate: [AuthGuard] },
    { path: 'promotion-event', component: PromotionEventComponent, canActivate: [AuthGuard] }
  ]},
  { path: 'workqueue', component: WorkQueueComponent, canActivate: [AuthGuard] },
  { path: 'location', component: LocationComponent, canActivate: [AuthGuard] },
  { path: 'location/:id', component: LocationComponent, canActivate: [AuthGuard] },
  { path: 'summary/:id', component: SummaryComponent, canActivate: [AuthGuard] },
  { path: 'decision/:id', component: DecisionComponent, canActivate: [AuthGuard] },
  { path: 'search', component: SearchComponent, canActivate: [AuthGuard] },
  { path: 'login', component: Login }
];
