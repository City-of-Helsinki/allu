import {Injectable} from '@angular/core';
import {Routes, Resolve, ActivatedRouteSnapshot} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import '../../rxjs-extensions.ts';

import {ApplicationComponent} from './info/application.component.ts';
import {LocationComponent} from '../application/location/location.component';
import {OutdoorEventComponent} from './info/outdoor-event/outdoor-event.component';
import {PromotionEventComponent} from './info/promotion-event/promotion-event.component';
import {TypeComponent} from '../../feature/application/type/type.component';
import {AuthGuard} from '../../feature/login/auth-guard.service';
import {ApplicationResolve} from './application-resolve';
import {ShortTermRentalComponent} from './info/short-term-rental/short-term-rental.component.ts';
import {CableReportComponent} from './info/cable-report/cable-report.component';
import {applicationCategories} from './type/application-category';

const childRoutes: Routes = [
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
  { path: 'OTHER_SHORT_TERM_RENTAL', component: ShortTermRentalComponent, canActivate: [AuthGuard] },
  // Cable reports
  { path: 'CITY_STREET_AND_GREEN', component: CableReportComponent, canActivate: [AuthGuard]},
  { path: 'WATER_AND_SEWAGE', component: CableReportComponent, canActivate: [AuthGuard]},
  { path: 'HKL', component: CableReportComponent, canActivate: [AuthGuard]},
  { path: 'ELECTRIC_CABLE', component: CableReportComponent, canActivate: [AuthGuard]},
  { path: 'DISTRICT_HEATING', component: CableReportComponent, canActivate: [AuthGuard]},
  { path: 'DISTRICT_COOLING', component: CableReportComponent, canActivate: [AuthGuard]},
  { path: 'TELECOMMUNICATION', component: CableReportComponent, canActivate: [AuthGuard]},
  { path: 'GAS', component: CableReportComponent, canActivate: [AuthGuard]},
  { path: 'AD_PILLARS_AND_STOPS', component: CableReportComponent, canActivate: [AuthGuard]},
  { path: 'PROPERTY_MERGER', component: CableReportComponent, canActivate: [AuthGuard]},
  { path: 'SOIL_INVESTIGATION', component: CableReportComponent, canActivate: [AuthGuard]},
  { path: 'JOINT_MUNICIPAL_INFRASTRUCTURE', component: CableReportComponent, canActivate: [AuthGuard]},
  { path: 'OTHER_CABLE_REPORT', component: CableReportComponent, canActivate: [AuthGuard]}
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
