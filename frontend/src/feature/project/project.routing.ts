import {Routes} from '@angular/router';

import {ProjectSummaryComponent} from './summary/project-summary.component.ts';
import {ProjectEditComponent} from './edit/project-edit.component.ts';
import {ProjectSearchComponent} from './search/project-search.component';
import {ProjectComponent} from './project.component';
import {ProjectApplicationsComponent} from './applications/project-applications.component';
import {ProjectResolve} from './project-resolve';

export const projectRoutes: Routes = [
  { path: 'projects', children: [
    { path: '', component: ProjectEditComponent, resolve: { project: ProjectResolve }},
    { path: ':id', children: [
      { path: '', component: ProjectComponent, children: [
        { path: '', resolve: { project: ProjectResolve }, children: [
          { path: '', redirectTo: 'summary' },
          { path: 'summary', component: ProjectSummaryComponent },
          { path: 'applications', component: ProjectApplicationsComponent }
        ]}
      ]},
      { path: 'edit', component: ProjectEditComponent, resolve: { project: ProjectResolve }}
    ]}
  ]},
  { path: 'projectSearch', component: ProjectSearchComponent }
];
