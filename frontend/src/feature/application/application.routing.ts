import {Injectable} from '@angular/core';
import {Routes, Resolve, ActivatedRouteSnapshot} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import '../../rxjs-extensions.ts';

import {ApplicationComponent} from '../../feature/application/application.component.ts';
import {OutdoorEventComponent} from '../../feature/application/outdoor-event/outdoor-event.component';
import {PromotionEventComponent} from '../../feature/application/promotion-event/promotion-event.component';
import {TypeComponent} from '../../feature/application/type/type.component';
import {AuthGuard} from '../../feature/login/auth-guard.service';
import {ApplicationResolve} from './application-resolve';

const childRoutes: Routes = [
  { path: '', component: TypeComponent, canActivate: [AuthGuard] },
  { path: 'outdoor-event', component: OutdoorEventComponent, canActivate: [AuthGuard] },
  { path: 'promotion-event', component: PromotionEventComponent, canActivate: [AuthGuard] }
];

export const applicationRoutes: Routes = [
  {
    path: 'applications',
    component: ApplicationComponent,
    canActivate: [AuthGuard],
    resolve: {
      application: ApplicationResolve
    },
    children: childRoutes
  },
  {
    path: 'applications/:id',
    component: ApplicationComponent,
    canActivate: [AuthGuard],
    resolve: {
      application: ApplicationResolve
    },
    children: childRoutes
  },
  {
    path: 'applications/:id/summary',
    component: ApplicationComponent,
    canActivate: [AuthGuard],
    resolve: {
      application: ApplicationResolve
    },
    children: childRoutes
  }
];
