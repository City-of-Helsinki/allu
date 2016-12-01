import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MdToolbarModule, MdCardModule, MdSidenavModule} from '@angular/material';
import {MapModule} from '../map/map.module';
import {MapSearchComponent} from './mapsearch.component';
import {SearchBarModule} from '../searchbar/searchbar.module';
import {ApplicationListComponent} from '../applicationlist/application-list.component';
import {AlluCommonModule} from '../common/allu-common.module';

@NgModule({
  imports: [
    CommonModule,
    MdToolbarModule,
    MdCardModule,
    MdSidenavModule,
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
