import {Routes} from '@angular/router';

import {ProjectSummaryComponent} from './summary/project-summary.component';
import {ProjectEditComponent} from './edit/project-edit.component';
import {ProjectSearchComponent} from './search/project-search.component';
import {ProjectComponent} from './project.component';
import {ProjectApplicationListComponent} from './applications/project-application-list.component';
import {ProjectResolve} from './project-resolve';
import {ProjectProjectsComponent} from './projects/project-projects.component';
import {AuthGuard} from '../../service/authorization/auth-guard.service';

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
          { path: 'applications', component: ProjectApplicationListComponent },
          { path: 'projects', component: ProjectProjectsComponent }
        ]}
      ]},
      { path: 'edit', component: ProjectEditComponent, resolve: { project: ProjectResolve }}
    ]}
  ]}
];
