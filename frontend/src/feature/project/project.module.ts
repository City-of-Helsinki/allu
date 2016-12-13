import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MdCardModule} from '@angular/material';

import {AlluCommonModule} from '../common/allu-common.module';
import {MapModule} from '../map/map.module';
import {SidebarModule} from '../sidebar/sidebar.module';
import {ProjectEditComponent} from './edit/project-edit.component';
import {projectRoutes} from './project.routing';
import {ProjectHub} from '../../service/project/project-hub';
import {ProjectService} from '../../service/project/project.service';
import {ProjectSearchComponent} from './search/project-search.component';
import {ProjectSummaryComponent} from './summary/project-summary.component';
import {ProjectComponent} from './project.component';
import {ProjectApplicationsComponent} from './applications/project-applications.component';
import {ProjectResolve} from './project-resolve';

@NgModule({
  imports: [
    AlluCommonModule,
    RouterModule.forChild(projectRoutes),
    FormsModule,
    ReactiveFormsModule,
    MdCardModule,
    MapModule,
    SidebarModule
  ],
  declarations: [
    ProjectComponent,
    ProjectEditComponent,
    ProjectSearchComponent,
    ProjectSummaryComponent,
    ProjectApplicationsComponent
  ],
  providers: [
    ProjectHub,
    ProjectService,
    ProjectResolve
  ]
})
export class ProjectModule {}

