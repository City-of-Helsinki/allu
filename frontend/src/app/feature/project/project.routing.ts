import {Routes} from '@angular/router';

import {ProjectSummaryComponent} from './summary/project-summary.component';
import {ProjectEditComponent} from './edit/project-edit.component';
import {ProjectSearchComponent} from './search/project-search.component';
import {ProjectComponent} from './project.component';
import {ProjectResolve} from './project-resolve';
import {AuthGuard} from '../../service/authorization/auth-guard.service';
import {CanDeactivateGuard} from '../../service/common/can-deactivate-guard';
import {ProjectCommentsComponent} from './comments/project-comments.component';
import {ProjectHistoryComponent} from './history/project-history.component';
import {RelatedProjectsComponent} from './related-projects/related-projects.component';

export const projectRoutes: Routes = [
  { path: 'projects', canActivate: [AuthGuard], children: [
    { path: '', redirectTo: 'search', pathMatch: 'full' },
    { path: 'search', component: ProjectSearchComponent },
    { path: 'edit', component: ProjectEditComponent, resolve: { project: ProjectResolve }},
    { path: ':id', children: [
      { path: '', component: ProjectComponent, resolve: { project: ProjectResolve }, children: [
        { path: '', children: [
          { path: '', redirectTo: 'info', pathMatch: 'full' },
          { path: 'info', component: ProjectSummaryComponent},
          { path: 'projects', component: RelatedProjectsComponent, canActivate: [AuthGuard] },
          { path: 'comments', component: ProjectCommentsComponent, canActivate: [AuthGuard], canDeactivate: [CanDeactivateGuard] },
          { path: 'history', component: ProjectHistoryComponent, canActivate: [AuthGuard] },
        ]}
      ]},
      { path: 'edit', component: ProjectEditComponent, resolve: { project: ProjectResolve }}
    ]}
  ]}
];
