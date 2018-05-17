import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatCardModule, MatTableModule, MatSortModule, MatPaginatorModule} from '@angular/material';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';

import {AlluCommonModule} from '../common/allu-common.module';
import {MapModule} from '../map/map.module';
import {SidebarModule} from '../sidebar/sidebar.module';
import {ProjectEditComponent} from './edit/project-edit.component';
import {projectRoutes} from './project.routing';
import {ProjectService} from '../../service/project/project.service';
import {ProjectSearchComponent} from './search/project-search.component';
import {ProjectSummaryComponent} from './summary/project-summary.component';
import {ProjectComponent} from './project.component';
import {ProjectApplicationListComponent} from './applications/project-application-list.component';
import {ProjectResolve} from './project-resolve';
import {ProjectState} from '../../service/project/project-state';
import {reducers} from './reducers';
import {ApplicationEffects} from './effects/application-effects';
import {ApplicationSelectComponent} from './applications/application-select.component';
import {SearchEffects} from './effects/search-effects';
import {ProjectApplicationsComponent} from './applications/project-applications.component';
import {ProjectHeaderComponent} from './header/project-header.component';
import {ParentProjectEffects} from './effects/parent-project-effects';
import {ChildProjectEffects} from './effects/child-project-effects';
import {ProjectInfoComponent} from 'app/feature/project/info/project-info.component';
import {ProjectEffects} from './effects/project-effects';
import {ApplicationBasketEffects} from './effects/application-basket-effects';
import {ProjectCommentsComponent} from './comments/project-comments.component';
import {CommentModule} from '../comment/comment.module';

@NgModule({
  imports: [
    AlluCommonModule,
    RouterModule.forChild(projectRoutes),
    FormsModule,
    ReactiveFormsModule,
    MatCardModule,
    MatTableModule,
    MatSortModule,
    MapModule,
    SidebarModule,
    MatTableModule,
    MatSortModule,
    MatPaginatorModule,
    StoreModule.forFeature('project', reducers),
    EffectsModule.forFeature([
      ProjectEffects,
      ApplicationEffects,
      SearchEffects,
      ParentProjectEffects,
      ChildProjectEffects,
      ApplicationBasketEffects
    ]),
    CommentModule
  ],
  declarations: [
    ProjectComponent,
    ProjectHeaderComponent,
    ProjectEditComponent,
    ProjectSearchComponent,
    ProjectSummaryComponent,
    ProjectInfoComponent,
    ProjectApplicationsComponent,
    ProjectApplicationListComponent,
    ApplicationSelectComponent,
    ProjectCommentsComponent
  ],
  providers: [
    ProjectService,
    ProjectResolve,
    ProjectState
  ]
})
export class ProjectModule {}

