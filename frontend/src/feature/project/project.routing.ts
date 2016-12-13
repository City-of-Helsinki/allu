import {Routes} from '@angular/router';

import {ProjectSummaryComponent} from './summary/project-summary.component.ts';
import {ProjectEditComponent} from './edit/project-edit.component.ts';
import {ProjectSearchComponent} from './search/project-search.component';
import {ProjectComponent} from './project.component';
import {ProjectApplicationsComponent} from './applications/project-applications.component';
import {ProjectResolve} from './project-resolve';
import {ProjectProjectsComponent} from './projects/project-projects.component';

export const projectRoutes: Routes = [
  { path: 'projects', children: [
    { path: '', component: ProjectEditComponent, resolve: { project: ProjectResolve }},
    { path: ':id', children: [
      { path: '', component: ProjectComponent, resolve: { project: ProjectResolve }, children: [
        { path: '', children: [
          { path: '', redirectTo: 'summary' },
          { path: 'summary', component: ProjectSummaryComponent },
          { path: 'applications', component: ProjectApplicationsComponent },
          { path: 'projects', component: ProjectProjectsComponent }
        ]}
      ]},
      { path: 'edit', component: ProjectEditComponent, resolve: { project: ProjectResolve }}
    ]}
  ]},
  { path: 'projectSearch', component: ProjectSearchComponent }
];
