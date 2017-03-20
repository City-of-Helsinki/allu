import {Routes} from '@angular/router';

import {ApplicationComponent} from './info/application.component';
import {LocationComponent} from '../application/location/location.component';
import {AuthGuard} from '../../feature/login/auth-guard.service';
import {ApplicationResolve} from './application-resolve';
import {ApplicationInfoComponent} from './info/application-info.component';
import {SearchComponent} from '../search/search.component';
import {AttachmentsComponent} from './attachment/attachments.component';
import {CommentsComponent} from './comment/comments.component';
import {ApplicationHistoryComponent} from './history/application-history.component';
import {DecisionPreviewComponent} from './decision-preview/decision-preview.component';

export const applicationTabs: Routes = [
  { path: '', redirectTo: 'info', pathMatch: 'full' },
  { path: 'info', component: ApplicationInfoComponent, canActivate: [AuthGuard] },
  { path: 'attachments', component: AttachmentsComponent, canActivate: [AuthGuard] },
  { path: 'comments', component: CommentsComponent, canActivate: [AuthGuard] },
  { path: 'history', component: ApplicationHistoryComponent, canActivate: [AuthGuard] },
  { path: 'decision-preview', component: DecisionPreviewComponent, canActivate: [AuthGuard] }
];

export const applicationRoutes: Routes = [
  { path: 'applications', canActivate: [AuthGuard], resolve: { application: ApplicationResolve},
    children: [
    { path: '', canActivate: [AuthGuard], redirectTo: 'search', pathMatch: 'full' },
    { path: 'search', component: SearchComponent, canActivate: [AuthGuard] },
    { path: 'location', component: LocationComponent, canActivate: [AuthGuard]},
    { path: 'edit', component: ApplicationComponent, canActivate: [AuthGuard], children: applicationTabs }
  ]},
  { path:  'applications/:id', canActivate: [AuthGuard], resolve: { application: ApplicationResolve},
    children: [
    { path: 'location', component: LocationComponent, canActivate: [AuthGuard] },
    { path: 'edit', component: ApplicationComponent, canActivate: [AuthGuard], children: applicationTabs },
    { path: 'summary', component: ApplicationComponent, canActivate: [AuthGuard], children: applicationTabs }
  ]}
];
