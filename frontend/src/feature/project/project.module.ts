import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MdCardModule} from '@angular/material';

import {AlluCommonModule} from '../common/allu-common.module';
import {ProjectComponent} from './project.component';
import {projectRoutes} from './project.routing';
import {ProjectHub} from '../../service/project/project-hub';
import {ProjectService} from '../../service/project/project.service';
import {ProjectSearchComponent} from './search/project-search.component';
import {ProjectSummaryComponent} from './summary/project-summary.component';
import {MapModule} from '../map/map.module';

@NgModule({
  imports: [
    AlluCommonModule,
    RouterModule.forChild(projectRoutes),
    FormsModule,
    ReactiveFormsModule,
    MdCardModule,
    MapModule
  ],
  declarations: [
    ProjectComponent,
    ProjectSearchComponent,
    ProjectSummaryComponent
  ],
  providers: [
    ProjectHub,
    ProjectService
  ]
})
export class ProjectModule {}

