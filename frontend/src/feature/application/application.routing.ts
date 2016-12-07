import {Routes} from '@angular/router';

import {ApplicationComponent} from './info/application.component.ts';
import {LocationComponent} from '../application/location/location.component';
import {EventComponent} from './info/event/event.component';
import {AuthGuard} from '../../feature/login/auth-guard.service';
import {ApplicationResolve} from './application-resolve';
import {ShortTermRentalComponent} from './info/short-term-rental/short-term-rental.component.ts';
import {CableReportComponent} from './info/cable-report/cable-report.component';

const childRoutes: Routes = [
  { path: 'EVENT', component: EventComponent, canActivate: [AuthGuard] },
  { path: 'SHORT_TERM_RENTAL', component: ShortTermRentalComponent, canActivate: [AuthGuard] },
  { path: 'CABLE_REPORT', component: CableReportComponent, canActivate: [AuthGuard]}
];

export const applicationRoutes: Routes = [
  { path: 'applications', canActivate: [AuthGuard], children: [
    { path: '', canActivate: [AuthGuard], redirectTo: 'location', pathMatch: 'full' },
    {
      path: 'location',
      component: LocationComponent,
      canActivate: [AuthGuard],
      resolve: { application: ApplicationResolve } },
    {
      path: 'info',
      component: ApplicationComponent,
      canActivate: [AuthGuard],
      resolve: { application: ApplicationResolve },
      children: childRoutes
    }
  ]},
  { path:  'applications/:id', canActivate: [AuthGuard], children: [
    { path: 'location',
      component: LocationComponent,
      canActivate: [AuthGuard],
      resolve: { application: ApplicationResolve }},
    {
      path: 'info',
      component: ApplicationComponent,
      canActivate: [AuthGuard],
      resolve: { application: ApplicationResolve },
      children: childRoutes
    },
    {
      path: 'summary',
      component: ApplicationComponent,
      canActivate: [AuthGuard],
      resolve: { application: ApplicationResolve },
      children: childRoutes
    }
  ]}
];
