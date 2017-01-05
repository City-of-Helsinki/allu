import {Routes} from '@angular/router';

import {ApplicationComponent} from './info/application.component.ts';
import {LocationComponent} from '../application/location/location.component';
import {AuthGuard} from '../../feature/login/auth-guard.service';
import {ApplicationResolve} from './application-resolve';
import {ApplicationInfoComponent} from './info/application-info.component';
import {SearchComponent} from '../search/search.component';
import {AttachmentsComponent} from './info/attachment/attachments.component';

export const applicationTabs: Routes = [
  { path: '', redirectTo: 'info' },
  {
    path: 'info',
    component: ApplicationInfoComponent,
    canActivate: [AuthGuard],
    resolve: { application: ApplicationResolve }
  },
  {
    path: 'attachments',
    component: AttachmentsComponent,
    canActivate: [AuthGuard],
    resolve: { application: ApplicationResolve }
  }
];

export const applicationRoutes: Routes = [
  { path: 'applications', canActivate: [AuthGuard], children: [
    { path: '', canActivate: [AuthGuard], redirectTo: 'search', pathMatch: 'full' },
    { path: 'search', component: SearchComponent, canActivate: [AuthGuard] },
    {
      path: 'location',
      component: LocationComponent,
      canActivate: [AuthGuard],
      resolve: { application: ApplicationResolve } },
    {
      path: 'edit',
      component: ApplicationComponent,
      canActivate: [AuthGuard],
      resolve: { application: ApplicationResolve },
      children: applicationTabs
    }
  ]},
  { path:  'applications/:id', canActivate: [AuthGuard], children: [
    { path: 'location',
      component: LocationComponent,
      canActivate: [AuthGuard],
      resolve: { application: ApplicationResolve }},
    {
      path: 'edit',
      component: ApplicationComponent,
      canActivate: [AuthGuard],
      resolve: { application: ApplicationResolve },
      children: applicationTabs
    },
    {
      path: 'summary',
      component: ApplicationComponent,
      canActivate: [AuthGuard],
      resolve: { application: ApplicationResolve },
      children: applicationTabs
    }
  ]}
];
