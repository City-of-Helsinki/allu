import {Routes} from '@angular/router';

import {ProjectSummaryComponent} from './summary/project-summary.component.ts';
import {ProjectEditComponent} from './edit/project-edit.component.ts';
import {ProjectSearchComponent} from './search/project-search.component';
import {ProjectComponent} from './project.component';
import {ProjectApplicationsComponent} from './applications/project-applications.component';
import {ProjectResolve} from './project-resolve';
import {ProjectProjectsComponent} from './projects/project-projects.component';
import {AuthGuard} from '../login/auth-guard.service';

export const projectRoutes: Routes = [
  { path: 'projects', canActivate: [AuthGuard], children: [
    { path: '', redirectTo: 'search', pathMatch: 'full' },
    { path: 'search', component: ProjectSearchComponent },
    { path: 'edit', component: ProjectEditComponent, resolve: { project: ProjectResolve }},
    { path: ':id', children: [
      { path: '', component: ProjectComponent, resolve: { project: ProjectResolve }, children: [
        { path: '', children: [
          { path: '', redirectTo: 'info' },
          { path: 'info', component: ProjectSummaryComponent},
          { path: 'applications', component: ProjectApplicationsComponent },
          { path: 'projects', component: ProjectProjectsComponent }
        ]}
      ]},
      { path: 'edit', component: ProjectEditComponent, resolve: { project: ProjectResolve }}
    ]}
  ]}
];
