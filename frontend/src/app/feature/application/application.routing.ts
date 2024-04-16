import {Routes} from '@angular/router';

import {ApplicationComponent} from './info/application.component';
import {LocationComponent} from '../application/location/location.component';
import {AuthGuard} from '../../service/authorization/auth-guard.service';
import {ApplicationResolve} from './application-resolve';
import {ApplicationInfoComponent} from './info/application-info.component';
import {SearchComponent} from '../search/search.component';
import {AttachmentsComponent} from './attachment/attachments.component';
import {ApplicationHistoryComponent} from './history/application-history.component';
import {InvoicingComponent} from './invoicing/invoicing.component';
import {DecisionComponent} from '../decision/decision.component';
import {SupervisionComponent} from './supervision/supervision.component';
import {CanDeactivateGuard} from '../../service/common/can-deactivate-guard';
import {ApplicationCommentsComponent} from './comment/application-comments.component';
import {DecisionDocumentComponent} from '@feature/decision/documents/decision-document.component';
import {DecisionTabResolve} from '@feature/decision/decision-tab-resolve';
import {ContractGuard} from '@feature/decision/documents/contract-guard';
import {InformationAcceptanceEntryComponent} from '@feature/information-request/acceptance/information-acceptance-entry.component';
import {InformationRequestSummariesComponent} from '@feature/information-request/summary/information-request-summaries.component';
import {InformationRequestEntryComponent} from '@feature/information-request/acceptance/information-request-entry.component';
import {InformationAcceptanceResolve} from '@feature/information-request/acceptance/information-acceptance-resolve';

export const decisionTabs: Routes = [
  { path: '', redirectTo: 'contract', pathMatch: 'full'},
  { path: 'contract', component: DecisionDocumentComponent, canActivate: [AuthGuard, ContractGuard], resolve: {tab: DecisionTabResolve} },
  { path: 'decision', component: DecisionDocumentComponent, canActivate: [AuthGuard], resolve: {tab: DecisionTabResolve} },
  { path: 'operational_condition', component: DecisionDocumentComponent, canActivate: [AuthGuard], resolve: {tab: DecisionTabResolve} },
  { path: 'work_finished', component: DecisionDocumentComponent, canActivate: [AuthGuard], resolve: {tab: DecisionTabResolve} },
  { path: 'termination', component: DecisionDocumentComponent, canActivate: [AuthGuard], resolve: {tab: DecisionTabResolve} },
];

export const informationRequest: Routes = [
  {
    path: 'pending_info',
    component: InformationAcceptanceEntryComponent,
    canActivate: [AuthGuard],
    resolve: { acceptanceData: InformationAcceptanceResolve }
  },
  {
    path: 'pending_info/:id',
    component: InformationAcceptanceEntryComponent,
    canActivate: [AuthGuard],
    resolve: { acceptanceData: InformationAcceptanceResolve }
  },
  { path: 'information_request', component: InformationRequestEntryComponent, canActivate: [AuthGuard]}
];

export const applicationTabs: Routes = [
  { path: '', redirectTo: 'info', pathMatch: 'full' },
  { path: 'info', component: ApplicationInfoComponent, canActivate: [AuthGuard], canDeactivate: [CanDeactivateGuard],
    children: informationRequest },
  { path: 'attachments', component: AttachmentsComponent, canActivate: [AuthGuard], canDeactivate: [CanDeactivateGuard] },
  { path: 'comments', component: ApplicationCommentsComponent, canActivate: [AuthGuard], canDeactivate: [CanDeactivateGuard] },
  { path: 'history', component: ApplicationHistoryComponent, canActivate: [AuthGuard] },
  { path: 'decision', component: DecisionComponent, canActivate: [AuthGuard], children: decisionTabs },
  { path: 'supervision', component: SupervisionComponent, canActivate: [AuthGuard] },
  { path: 'invoicing', component: InvoicingComponent, canActivate: [AuthGuard], canDeactivate: [CanDeactivateGuard] },
  { path: 'supplements', component: InformationRequestSummariesComponent, canActivate: [AuthGuard], children: informationRequest }
];

export const applicationRoutes: Routes = [
  { path: 'applications', canActivate: [AuthGuard],
    children: [
    { path: '', redirectTo: 'search', pathMatch: 'full' },
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
    { path: 'summary', component: ApplicationComponent, canActivate: [AuthGuard], children: applicationTabs }
  ]}
];

