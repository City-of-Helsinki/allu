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

@NgModule({
  imports: [
    AlluCommonModule,
    RouterModule.forChild(projectRoutes),
    FormsModule,
    ReactiveFormsModule,
    MdCardModule
  ],
  declarations: [
    ProjectComponent,
    ProjectSearchComponent
  ],
  providers: [
    ProjectHub,
    ProjectService
  ]
})
export class ProjectModule {}

