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
import {ShortTermRentalComponent} from './short-term-rental/short-term-rental.component.ts';

const childRoutes: Routes = [
  { path: '', component: TypeComponent, canActivate: [AuthGuard] },
  // Street work
  { path: 'DIG_NOTICE', component: PromotionEventComponent, canActivate: [AuthGuard] },
  { path: 'AREA_RENTAL', component: PromotionEventComponent, canActivate: [AuthGuard] },
  { path: 'TEMPORARY_TRAFFIC_ARRANGEMENTS', component: PromotionEventComponent, canActivate: [AuthGuard] },
  // Events
  { path: 'OUTDOOREVENT', component: OutdoorEventComponent, canActivate: [AuthGuard] },
  { path: 'PROMOTION', component: PromotionEventComponent, canActivate: [AuthGuard] },
  { path: 'ELECTION', component: PromotionEventComponent, canActivate: [AuthGuard] },
  // Short term rental
  { path: 'BRIDGE_BANNER', component: ShortTermRentalComponent, canActivate: [AuthGuard] },
  { path: 'BENJI', component: ShortTermRentalComponent, canActivate: [AuthGuard] },
  { path: 'PROMOTION_OR_SALES', component: ShortTermRentalComponent, canActivate: [AuthGuard] },
  { path: 'URBAN_FARMING', component: ShortTermRentalComponent, canActivate: [AuthGuard] },
  { path: 'MAIN_STREET_SALES', component: ShortTermRentalComponent, canActivate: [AuthGuard] },
  { path: 'SUMMER_THEATER', component: ShortTermRentalComponent, canActivate: [AuthGuard] },
  { path: 'DOG_TRAINING_FIELD', component: ShortTermRentalComponent, canActivate: [AuthGuard] },
  { path: 'DOG_TRAINING_EVENT', component: ShortTermRentalComponent, canActivate: [AuthGuard] },
  { path: 'CARGO_CONTAINER', component: ShortTermRentalComponent, canActivate: [AuthGuard] },
  { path: 'SMALL_ART_AND_CULTURE', component: ShortTermRentalComponent, canActivate: [AuthGuard] },
  { path: 'SEASON_SALE', component: ShortTermRentalComponent, canActivate: [AuthGuard] },
  { path: 'CIRCUS', component: ShortTermRentalComponent, canActivate: [AuthGuard] },
  { path: 'ART', component: ShortTermRentalComponent, canActivate: [AuthGuard] },
  { path: 'STORAGE_AREA', component: ShortTermRentalComponent, canActivate: [AuthGuard] },
  { path: 'OTHER_SHORT_TERM_RENTAL', component: ShortTermRentalComponent, canActivate: [AuthGuard] }
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
