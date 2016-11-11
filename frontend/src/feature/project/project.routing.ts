import {Routes} from '@angular/router';

import {ProjectSummaryComponent} from './summary/project-summary.component.ts';
import {ProjectComponent} from './project.component.ts';

export const projectRoutes: Routes = [
  { path: 'projects', component: ProjectComponent },
  { path: 'projects/:id', component: ProjectComponent },
  { path: 'projects/summary', component: ProjectSummaryComponent }
];
