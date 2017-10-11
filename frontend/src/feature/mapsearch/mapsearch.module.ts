import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {CommonModule} from '@angular/common';
import {MatToolbarModule, MatCardModule, MatSidenavModule, MatTooltipModule} from '@angular/material';
import {MapModule} from '../map/map.module';
import {MapSearchComponent} from './mapsearch.component';
import {SearchBarModule} from '../searchbar/searchbar.module';
import {ApplicationListComponent} from '../applicationlist/application-list.component';
import {AlluCommonModule} from '../common/allu-common.module';


@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    MatToolbarModule,
    MatCardModule,
    MatSidenavModule,
    MatTooltipModule,
    AlluCommonModule,
    MapModule,
    SearchBarModule
  ],
  declarations: [
    MapSearchComponent,
    ApplicationListComponent
  ]
})
export class MapSearchModule {}
