import {Routes} from '@angular/router';

import {ApplicationComponent} from './info/application.component.ts';
import {LocationComponent} from '../application/location/location.component';
import {EventComponent} from './info/event/event.component';
import {AuthGuard} from '../../feature/login/auth-guard.service';
import {ApplicationResolve} from './application-resolve';
import {ShortTermRentalComponent} from './info/short-term-rental/short-term-rental.component.ts';
import {CableReportComponent} from './info/cable-report/cable-report.component';
import {ExcavationAnnouncementComponent} from './info/excavation-announcement/excavation-announcement.component';
import {NoteComponent} from './info/note/note.component';
import {ApplicationInfoComponent} from './info/application-info.component';
import {SearchComponent} from '../search/search.component';

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
      children: [
        { path: 'info',
          component: ApplicationInfoComponent,
          canActivate: [AuthGuard],
          resolve: { application: ApplicationResolve }
        }
      ]
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
      children: [
        { path: '', redirectTo: 'info' },
        { path: 'info',
          component: ApplicationInfoComponent,
          canActivate: [AuthGuard],
          resolve: { application: ApplicationResolve }
        }
      ]
    },
    {
      path: 'summary',
      component: ApplicationComponent,
      canActivate: [AuthGuard],
      resolve: { application: ApplicationResolve },
      children: [
        { path: '', redirectTo: 'info' },
        { path: 'info',
          component: ApplicationInfoComponent,
          canActivate: [AuthGuard],
          resolve: { application: ApplicationResolve }
        }
      ]
    }
  ]}
];
