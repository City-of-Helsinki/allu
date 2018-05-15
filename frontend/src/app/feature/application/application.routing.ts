import {Routes} from '@angular/router';

import {ApplicationComponent} from './info/application.component';
import {LocationComponent} from '../application/location/location.component';
import {AuthGuard} from '../../service/authorization/auth-guard.service';
import {ApplicationResolve} from './application-resolve';
import {ApplicationInfoComponent} from './info/application-info.component';
import {SearchComponent} from '../search/search.component';
import {AttachmentsComponent} from './attachment/attachments.component';
import {ApplicationHistoryComponent} from './history/application-history.component';
import {DecisionPreviewComponent} from './decision-preview/decision-preview.component';
import {InvoicingComponent} from './invoicing/invoicing.component';
import {DecisionComponent} from '../decision/decision.component';
import {SupervisionComponent} from './supervision/supervision.component';
import {CanDeactivateGuard} from '../../service/common/can-deactivate-guard';
import {ApplicationCommentsComponent} from './comment/application-comments.component';

export const applicationTabs: Routes = [
  { path: '', redirectTo: 'info', pathMatch: 'full' },
  { path: 'info', component: ApplicationInfoComponent, canActivate: [AuthGuard], canDeactivate: [CanDeactivateGuard] },
  { path: 'attachments', component: AttachmentsComponent, canActivate: [AuthGuard], canDeactivate: [CanDeactivateGuard] },
  { path: 'comments', component: ApplicationCommentsComponent, canActivate: [AuthGuard], canDeactivate: [CanDeactivateGuard] },
  { path: 'history', component: ApplicationHistoryComponent, canActivate: [AuthGuard] },
  { path: 'decision-preview', component: DecisionPreviewComponent, canActivate: [AuthGuard] },
  { path: 'supervision', component: SupervisionComponent, canActivate: [AuthGuard] },
  { path: 'invoicing', component: InvoicingComponent, canActivate: [AuthGuard], canDeactivate: [CanDeactivateGuard] }
];

export const applicationRoutes: Routes = [
  { path: 'applications', canActivate: [AuthGuard],
    children: [
    { path: '', canActivate: [AuthGuard], redirectTo: 'search', pathMatch: 'full' },
    { path: 'search', component: SearchComponent, canActivate: [AuthGuard] },
    { path: 'location', component: LocationComponent, canActivate: [AuthGuard],
      resolve: { application: ApplicationResolve} },
    { path: 'edit', component: ApplicationComponent, canActivate: [AuthGuard],
      resolve: { application: ApplicationResolve}, children: applicationTabs }
  ]},
  { path:  'applications/:id', canActivate: [AuthGuard], resolve: { application: ApplicationResolve},
    children: [
    { path: 'location', component: LocationComponent, canActivate: [AuthGuard] },
    { path: 'edit', component: ApplicationComponent, canActivate: [AuthGuard], children: applicationTabs },
    { path: 'summary', component: ApplicationComponent, canActivate: [AuthGuard], children: applicationTabs },
    { path: 'decision', component: DecisionComponent, canActivate: [AuthGuard]}
  ]}
];
