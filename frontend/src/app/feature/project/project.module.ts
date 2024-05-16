import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatLegacyCardModule as MatCardModule} from '@angular/material/legacy-card';
import {MatLegacyPaginatorModule as MatPaginatorModule} from '@angular/material/legacy-paginator';
import {MatSortModule} from '@angular/material/sort';
import {MatLegacyTableModule as MatTableModule} from '@angular/material/legacy-table';
import {StoreModule} from '@ngrx/store';
import {EffectsModule} from '@ngrx/effects';

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
import {reducersProvider, reducersToken} from './reducers';
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
import {HistoryModule} from '../history/history.module';
import {ProjectHistoryComponent} from './history/project-history.component';
import {RelatedProjectsComponent} from './related-projects/related-projects.component';
import {RelatedProjectListComponent} from './related-projects/related-project-list.component';
import {ProjectSelectComponent} from './related-projects/project-select.component';
import {CustomerRegistryModule} from '@feature/customerregistry/customer-registry.module';

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
    StoreModule.forFeature('project', reducersToken),
    EffectsModule.forFeature([
      ProjectEffects,
      ApplicationEffects,
      SearchEffects,
      ParentProjectEffects,
      ChildProjectEffects,
      ApplicationBasketEffects
    ]),
    CommentModule,
    HistoryModule,
    CustomerRegistryModule
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
    ProjectCommentsComponent,
    ProjectHistoryComponent,
    RelatedProjectsComponent,
    RelatedProjectListComponent,
    ProjectSelectComponent
  ],
  providers: [
    ProjectService,
    ProjectResolve,
    ProjectState,
    reducersProvider
  ]
})
export class ProjectModule {}

